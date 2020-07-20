<svelte:head>
  <title>Series de datos</title>
</svelte:head>

<script>
	import { onMount, beforeUpdate } from 'svelte';
	import echarts from 'echarts';

	export let selected = [];
	export let series = [];
	let totalPages = 0;

	function handleMostrar(id){
		selected.push(id);
	}
	
</script>

<style>
	table {
		border-collapse: collapse;
		border-spacing: 0;
		empty-cells: show;
        width: 100%;
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



<div>
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
					{#if selected.includes(serie.id)}
						<button on:click={() => handleMostrar(serie.id)}>
							Quitar
						</button>
					{:else}
						<button on:click={() => handleMostrar(serie.id)}>
							Mostrar
						</button>
					{/if}

				</td>
			</tr>
		{/each}
	</table>
	</div>