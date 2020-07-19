<svelte:head>
  <title>Series de datos</title>
</svelte:head>

<script>
	import { onMount, beforeUpdate } from 'svelte';
	import echarts from 'echarts';
	import LineChart from './components/LineChart.svelte';

	$: loading = true;
	$: mostrar = false;
	$: selected = [];
	$: page = 0;
	let series = [];
	
	onMount(async function getData() {
		const response = await self.fetch('http://localhost:8080/serie?page='+page);
		series = await response.json();
		loading = false;
	});

	function handleMostrar(id, descripcion){
		selected.push({id: id, label: descripcion});
		mostrar = true;
	}

	function handlePage(number){
		page = page + number
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
			<td>{serie.siglasFuente}</td>
			{#if serie.datos}
				<td>{serie.datos.length}</td>
			{:else}
				<td>0</td>
			{/if}
			<td>
				<button on:click={handleMostrar(serie.id, serie.descripcion)}>
					Mostrar
				</button>
			</td>
		</tr>
	{/each}
</table>
<button on:click={page=page-1} disabled={page < 1}>
	Anterior
</button>
<button on:click={page=page+1}>
	Siguiente
</button>
{/if}

{#if mostrar}
	<LineChart series={selected}/>
{/if}
page = {page}