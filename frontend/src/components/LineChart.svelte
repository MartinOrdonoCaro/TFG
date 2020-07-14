<script>
	import { onMount } from 'svelte';
    import echarts from 'echarts';
    
    export let id;
    $: loading = true;

    let canvas;

	onMount(async function() {
        const response = await self.fetch('http://localhost:8080/serie/'+id);
        let serie = await response.json();
        const serieData = [];
        serie.datos.forEach(element => {
            serieData.push([element.anio + element.periodo, element.valor]);
        });
        serieData.sort();

        
        var lineChart = echarts.init(canvas);

        var option = {
            toolbox: {
                show: true,
                feature: {
                    saveAsImage: {
                        show: true,
                        title: "Guardar como imagen"
                    }
                }
            },
            dataset: {source: serieData},
            xAxis: {
                type: 'category'
            },
            yAxis: {},
            series: [{
                type: 'line'
            }]
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