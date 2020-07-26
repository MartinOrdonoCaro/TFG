<svelte:head>
  <title>Series de datos</title>
</svelte:head>

<script>
	import { onMount, beforeUpdate, afterUpdate } from 'svelte';
	import { fade } from 'svelte/transition';
	import echarts from 'echarts';
	import LineChart from './LineChart.svelte';
	import Button, { Group, Label }from '@smui/button';
	import DataTable, {Head, Body, Row, Cell} from '@smui/data-table';
	import Paper, {Title, Subtitle, Content} from '@smui/paper';
	import Chip, {Set, Icon, Text} from '@smui/chips';
	import { Router, Link, Route } from "svelte-routing";
	import ExcelTableWrapper from "./../routes/ExcelTableWrapper.svelte";
	import Checkbox from '@smui/checkbox';
	
	
	export let page;

	let selected = {
		"ids": [],
		"codigos": []
	};
	let checkbox = [];
	let excel = false;
	let grafica = false;
	let totalPages;
	let currentPage;
	let series = [];

	beforeUpdate(() => {
		console.log("updating data"+page)
		if(currentPage != page){
			fetch('http://localhost:8080/serie?page='+page)
					.then(response => response.json())
					.then(jsonData => {	
						series = jsonData.content;
						if(totalPages != jsonData.totalPages){
							totalPages = jsonData.totalPages;
						}
					});
		}
	});

	afterUpdate(() => {
		currentPage = page;
		checkbox = selected.ids;
	});

	function handlePage(number){
		console.log("clicked");
		page = page + number;
	}

	function handleSelect(id){
		var codigo = series.find(serie => {
			return serie.id === id;
		}).codigo;
		
		if(selected.ids.includes(id)){
			selected.ids.splice(selected.ids.indexOf(id), 1);
			selected.codigos.splice(selected.codigos.indexOf(codigo), 1);
		}
		else {
			selected.ids.push(id);
			selected.codigos.push(codigo);
		}
		selected = selected;
	}

	function handleReset(){
		excel = grafica = false;
	}
	
</script>

{#if !excel && !grafica}
<Paper>
	<Title>Listado de series</Title>
	<Content>
		<DataTable table$aria-label="Series" >
			<Head>
				<Row>
					<Cell>Código</Cell>
					<Cell>Descripción</Cell>
					<Cell>Fuente</Cell>
					<Cell>Nº de Datos</Cell>
					<Cell>Periodicidad</Cell>
					<Cell checkbox>
						<Checkbox disabled={true} indeterminate={true}} />
					</Cell>
				</Row>
			</Head>
			<Body>
			{#each series as serie, i (i)}
				<Row>
					<Cell>{serie.codigo}</Cell>
					<Cell>{serie.descripcion}</Cell>
					<Cell>{serie.siglasFuente}</Cell>
					{#if serie.datos}
						<Cell>{serie.datos.length}</Cell>
					{:else}
						<Cell>0</Cell>
					{/if}
					<Cell>{serie.periodicidad}</Cell>
					<!-- <Cell>
						{#if selected.ids.includes(serie.id)}
							<Button on:click={() => handleSelect(serie.id)}>
								Quitar
							</Button>
						{:else}
							<Button on:click={() => handleSelect(serie.id)}>
								Mostrar
							</Button>
						{/if}

					</Cell> -->

					<Cell checkbox>
                		<Checkbox on:click={() => handleSelect(serie.id)} checked={selected.ids.includes(serie.id)}/>
              		</Cell>
				</Row>
			{/each}
			</Body>
		</DataTable>
	</Content>
	<br>
	<Content>
    	<Group>
		<Button variant="raised" on:click={() => handlePage(-page)} disabled={page < 1}>
			&#171
		</Button>
		<Button variant="raised" on:click={() => handlePage(-1)} disabled={page < 1}>
			&#60; Anterior
		</Button>
		<!-- {#if page > 0}
			<Button variant="raised" on:click={() => handlePage(-1)}>
				{page-1}
			</Button>
		{/if}
		{#if page > 1}
			<Button variant="raised" on:click={() => handlePage(-page)}>
				{0}
			</Button>
		{/if} -->
		<Button variant="outlined" disabled=true>
			{page}
		</Button>
		<!-- {#if page < totalPages-1}
			<Button variant="raised" on:click={() => handlePage(+1)}>
				{page+1}
			</Button>
		{/if}
		{#if page < totalPages-1}
			<Button variant="text" disabld={true}>...</Button>
			<Button variant="raised" on:click={() => handlePage(totalPages-1-page)}>
				{totalPages-1}
			</Button>
		{/if} -->
		<Button variant="raised" on:click={() => handlePage(1)} disabled={totalPages-1 <= page}>
			<Label>Siguiente ></Label>
		</Button>
		<Button variant="raised" on:click={() => handlePage(totalPages-1-page)} disabled={totalPages-1 <= page}>
			&#187
		</Button>
		</Group>
	</Content>

	<br>

	<Content>
	
	<Button variant="raised" on:click={() => excel = true} disabled={selected.ids.length < 1 }>
		Ver datos en Excel
	</Button>
	
	<Button variant="raised" on:click={() => grafica = true} disabled={selected.ids.length < 1 }>
		Ver datos en Gráfica
	</Button>


	<Set chips={selected.codigos} let:chip input>
  		<Chip ><Text>{chip}</Text></Chip>
	</Set>
	</Content>
	
</Paper>


{:else}
<Paper>
	{#if grafica}
		<LineChart bind:selected={selected.ids}/>
	{:else if excel}
		<Button variant="raised" on:click={() => handleReset()}>
			Volver
		</Button>
		<ExcelTableWrapper selected={selected.ids}/>
	{/if}
	<Button variant="raised" on:click={() => handleReset()}>
		Volver
	</Button>
</Paper>
{/if}
