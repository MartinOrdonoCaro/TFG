<svelte:head>
  <title>Series de datos</title>
</svelte:head>

<script>
	import { onMount, beforeUpdate } from 'svelte';
	import echarts from 'echarts';
	import LineChart from './components/LineChart.svelte';
	import DataTable from './components/DataTable.svelte';

	let loading = true;
	let selected = [];
	let chart;
	let page = 0;
	let totalPages = 0;
	let series = [];
	
	onMount(async () => {
		const response = await self.fetch('http://localhost:8080/serie?page='+page);
		const jsonResponse = await response.json();
		series = jsonResponse.content;
		totalPages = jsonResponse.totalPages;
		loading = false;
	});

	async function handlePage(number){
		loading = true
		page = page + number;
		const response = await self.fetch('http://localhost:8080/serie?page='+page);
		const jsonResponse = await response.json();
		series = jsonResponse.content;
		totalPages = jsonResponse.totalPages;
		loading = false;
	}
	
</script>


{#if loading}
	<h3>Loading...</h3>
{:else}
	<svelte:component this={DataTable} bind:selected={selected} bind:series={series} />
	<div>
	<button on:click|preventDefault={() => handlePage(-1)} disabled={page < 1}>
		Anterior
	</button>
	<button on:click|preventDefault={() => handlePage(1)} disabled={totalPages-1 <= page}>
		Siguiente
	</button>
	</div>
{/if}

<LineChart bind:selected={selected}/>
page = {page}
total page = {totalPages}
selected = {selected}