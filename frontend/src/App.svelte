<script>
	import { onMount } from 'svelte';
	import echarts from 'echarts';
	import LineChart from './components/LineChart.svelte';

	$: loading = true;
	$: selected = -1;
	let series = [];
	
	onMount(async function() {
		const response = await self.fetch('http://localhost:8080/serie');
		series = await response.json();
		loading = false;
	});

	function handleMostrar(id){
		selected = id;
	}
	
</script>

<style>
	table {
		border-collapse: collapse;
		border-spacing: 0;
		empty-cells: show;
	}
	table th {
		background-color: #eee;
	}
	table th, table td {
		padding: 5px;
	}
	table tr.odd {
		background-color: #eee;
	}
</style>


{#if loading}
<h3>Loading...</h3>
{:else}
<table>
	<tr>
		<th>Código</th>
		<th>Descripción</th>
		<th>Fuente</th>
		<th>Datos</th>
	</tr>
	{#each series as serie, i (i)}
		<tr class:odd={i%2}>
			<td>{serie.codigo}</td>
			<td>{serie.descripcion}</td>
			<td>{serie.fuente}</td>
			{#if serie.datos}
				<td>{serie.datos.length}</td>
			{:else}
				<td>0</td>
			{/if}
			<td>
				<button on:click={handleMostrar(serie.id)}>
					Mostrar
				</button>
			</td>
		</tr>
	{/each}
</table>
{/if}

{#if selected >= 0}
	<LineChart id={selected}/>
{:else}
 Pulse mostrar para visualizar la serie
{/if}