<script>
	import { onMount, beforeUpdate } from 'svelte';
    import echarts from 'echarts';
    import Checkbox from '@smui/checkbox';
    import FormField from '@smui/form-field';
    import ECharts from 'echarts-for-svelte';
    import infographic from 'echarts/theme/infographic.js'
    
    export let selected;
    let periodicidad;
    let option;
    const style = "height:600px";

	beforeUpdate(async function() {
        let periodos = [];
        const series = [];
        if(selected.length > 0) {
            for(const serie of selected){
                serie.datos.forEach(element => {
                    if(!periodos.includes(element.anio + " " + element.periodo)){
                        periodos.push(element.anio + " " + element.periodo);
                    };
                });
            };
            
            periodos.sort();
            for(const serie of selected){
                const serieData = [];
                const yData = [];
                const xData = [];

                serie.datos.forEach(element => {
                    serieData.push([element.anio + " " + element.periodo, element.valor]);
                });
                serieData.sort();
    
                let lastData;
                for(const periodo of periodos){
                    let found = false;
                    for(const dupla of serieData){
                        if (dupla[0] === periodo) {
                            xData.push(dupla[1]);  
                            found = true;
                        };
                    };
                    if(!found){
                        xData.push(null);
                    };
                };
                
                series.push({
                    type: 'line',
                    name: serie.descripcion,
                    data: xData,
                    symbolSize: 6,
                    connectNulls: true
                });
            };

            option = {
                legend: {},
                dataZoom: [
                    {
                        xAxisIndex: [0]
                    }
                ],
                toolbox: {
                    feature: {
                        saveAsImage: {
                            show: true
                        }
                    }
                },
                tooltip: {
                    trigger: 'axis'
                },
                xAxis: {
                    type: 'category',
                    data: periodos
                },
                yAxis: {
                    type: 'value'
                },
                series: series
            };
        }
    });
</script>
<div>
	<ECharts
		{echarts} {option} style={style}
	/>
</div>