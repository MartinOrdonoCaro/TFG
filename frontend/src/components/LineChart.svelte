<script>
	import { onMount, beforeUpdate } from 'svelte';
    import echarts from 'echarts';
    import Checkbox from '@smui/checkbox';
    import FormField from '@smui/form-field';
    
    export let selected;
    let periodicidad;
    let canvas;
    let lineChart;
    let tendencias = false;

    onMount(function() {
        lineChart = echarts.init(canvas);
    });

	beforeUpdate(async function() {
        let periodos = [];
        const series = [];
        if(lineChart){
            lineChart.clear();
        };
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
                const tendencia = [];

                serie.datos.forEach(element => {
                    serieData.push([element.anio + " " + element.periodo, element.valor]);
                });
                serieData.sort();
    
                let lastData;
                for(const periodo of periodos){
                    let found = false;
                    for(const dupla of serieData){
                        if (dupla[0] === periodo) {

                            if (tendencia.length < 1) {
                                tendencia.push(0);
                            } else {
                                console.log("valor: "+dupla[1]);
                                console.log("anterior: "+xData[lastData]);
                                console.log("lectura "+lastData);
                                tendencia.push(dupla[1]-lastData);
                            };

                            xData.push(dupla[1]);             
                            lastData = dupla[1];
                            found = true;
                        };
                    };
                    if(!found){
                        xData.push(null);
                        tendencia.push(null);
                    };
                };
                
                series.push({
                    type: 'line',
                    name: serie.descripcion,
                    data: xData,
                    symbolSize: 6,
                    connectNulls: true
                });
                if(tendencias){
                    series.push({
                        type: 'line',
                        name: "Tendencia: " + (selected.indexOf(serie)+1),
                        data: tendencia,
                        symbol: "triangle",
                        symbolSize: 6,
                        itemStyle: {
                            color: '#808080'
                        },
                        connectNulls: true
                    });
                };
            };

            var option = {
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
                    trigger: 'item',
                    axisPointer: {
                        type: 'cross'
                    }
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: periodos
                },
                yAxis: {
                    type: 'value'
                },
                series: series
            };
            lineChart.setOption(option);
        }
    });
</script>

<div>
    <FormField>
    <Checkbox bind:checked={tendencias} />
    <span slot="label">Mostrar tendencias.</span>
    </FormField>
</div>
<div>
	<canvas
		bind:this={canvas}
		width={1500}
		height={560}
	/>
</div>