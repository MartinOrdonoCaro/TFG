<script>
	import { onMount } from 'svelte';
    import echarts from 'echarts';
    
    export let series;
    $: loading = true;

    let canvas;

	onMount(async function() {
        const response = await self.fetch('http://localhost:8080/serie/'+series[0].id);
        let serie = await response.json();
        const serieData = [];
        serie.datos.forEach(element => {
            serieData.push([element.anio + element.periodo, element.valor]);
        });
        serieData.sort();

        
        var lineChart = echarts.init(canvas);

        var option = {
            legend: {
                data: [series[0].label]
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
                    }
                }
            },
            dataset: {source: serieData},
            xAxis: {
                type: 'category'
            },
            yAxis: {},
            series: [
                {
                    name: series[0].label,
                    type: 'line'
                }
            ]
        };
        lineChart.setOption(option);

        loading = false;
	});
</script>



	<canvas
		bind:this={canvas}
		width={1500}
		height={560}
	></canvas>