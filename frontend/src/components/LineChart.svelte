<script>
	import { onMount } from 'svelte';
    import echarts from 'echarts';
    
    export let selected;
    let series = [];
    $: loading = true;
    let periodicidad;
    let canvas;

	onMount(async function() {
        const response = await self.fetch('http://localhost:8080/serie/find-all-by-id?ids='+selected);
        series = await response.json().content;
        const source = [];
        const serieInfo = [];
        for(const serie of series){
            const serieData = [];
            const yData = ["serie"];
            const xData = [serie.descripcion];

            serie.datos.forEach(element => {
                serieData.push([element.anio + element.periodo, element.valor]);
            });
            serieData.sort();

            serieData.forEach(dupla => {
                yData.push(dupla[0]);
                xData.push(dupla[1]);
            });
                
            if (!source.length) {
                source.push(yData);
            };

            source.push(xData);

            console.log(source);
            console.log("fin");

            
            

        };

        
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


<div>
	<canvas
		bind:this={canvas}
		width={1500}
		height={560}
	/>
</div>