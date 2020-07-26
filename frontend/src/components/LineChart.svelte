<script>
	import { onMount, beforeUpdate } from 'svelte';
    import echarts from 'echarts';
    
    export let selected;
    let periodicidad;
    let canvas;
    let lineChart;

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
                const serieData = [];
                const yData = [];
                const xData = [];

                serie.datos.forEach(element => {
                    serieData.push([element.anio + " " + element.periodo, element.valor]);
                });
                serieData.sort();

                serieData.forEach(dupla => {
                    yData.push(dupla[0]);
                    xData.push(dupla[1]);
                });
                            
                if (periodos.length < 1) {
                    periodos = yData;
                };
                series.push({
                    type: 'line',
                    name: serie.descripcion,
                    data: xData
                })
            };

            var option = {
                legend: {
                },
                dataZoom: [
                    {
                        xAxisIndex: [0]
                    }
                ],
                toolbox: {
                    feature: {
                        dataView: {
                            show: true
                        },
                        saveAsImage: {
                            show: true
                        }
                    }
                },
                xAxis: {
                    type: 'category',
                    data: periodos
                },
                yAxis: {},
                series: series
            };
            lineChart.setOption(option);
        }
    });
</script>

<div>
	<canvas
		bind:this={canvas}
		width={1500}
		height={560}
	/>
</div>