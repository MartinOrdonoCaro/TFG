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

	let selected = [];
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
	});

	function handlePage(number){
		console.log("clicked");
		page = page + number;
	}

	function handleSelect(serie){		
		if(isSelected(serie)){
			selected.splice(getIndexSelected(serie), 1);
		}
		else {
			selected.push(serie);
		}
		selected = selected;
	}

	function isSelected(serie){
		if(selected.find(item => item.id === serie.id)){
			return true;
		} else {
			return false;
		}
	}

	function getIndexSelected(serie){
		const selectedSerie = selected.find(item => item.id === serie.id);
		if(selectedSerie){
			return selected.indexOf(selectedSerie);
		} else {
			return false;
		}
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

					<Cell checkbox>
                		<Checkbox on:click={() => handleSelect(serie)} checked={isSelected(serie)}/>
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
		
		<Button variant="outlined" disabled=true>
			{page}
		</Button>
		
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
	
	<Button variant="raised" on:click={() => excel = true} disabled={selected.length < 1 }>
		Ver datos en Excel
	</Button>
	
	<Button variant="raised" on:click={() => grafica = true} disabled={selected.length < 1 }>
		Ver datos en Gráfica
	</Button>


	<Set chips={selected} let:chip input>
  		<Chip ><Text>{chip.codigo}</Text></Chip>
	</Set>
	</Content>
	
</Paper>


{:else}
<Paper>
	{#if grafica}
		<LineChart bind:selected={selected}/>
	{:else if excel}
		<Button variant="raised" on:click={() => handleReset()}>
			Volver
		</Button>
		<ExcelTableWrapper selected={selected}/>
	{/if}
	<Button variant="raised" on:click={() => handleReset()}>
		Volver
	</Button>
</Paper>
{/if}
