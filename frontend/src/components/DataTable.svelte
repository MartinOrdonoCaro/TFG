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
	import Chip, {Set, Text} from '@smui/chips';
	import { Router, Link, Route } from "svelte-routing";
	import ExcelTableWrapper from "./../routes/ExcelTableWrapper.svelte";
	import Checkbox from '@smui/checkbox';
	import Select, {Option} from '@smui/select';
	import Textfield from '@smui/textfield'
	import IconButton, {Icon} from '@smui/icon-button';
	
	
	export let page;

	let selected = [];
	let excel = false;
	let grafica = false;
	let query = true;
	let totalPages;
	let currentPage;
	let series = [];
	let sizes = [5, 10, 15, 20];
	const params = {
		pageSize: 5,
		keyword: "",
		fuente: "",
		periodicidad: "",
		territorio: "",
		origen: "",
	};
	const filterOptions = {
		territorio: [],
		periodicidad: [],
		fuente: [],
		origen: []
	}

	onMount(() => {
		fetch('http://localhost:8080/serie/filtro')
					.then(response => response.json())
					.then(jsonData => {	
						filterOptions.territorio = jsonData.territorios;
						filterOptions.periodicidad = jsonData.periodos;
						filterOptions.fuente = jsonData.fuentes;
						filterOptions.origen = jsonData.origenes;
					});
	});
	beforeUpdate(() => {
		console.log("updating data: page " + page+ ", size: "+params.pageSize)
		
		if(query){
			var url = new URL('http://localhost:8080/serie');
			var urlSearch = new URLSearchParams(params);
			urlSearch.append("page", page);
			url.search = urlSearch.toString();
			fetch(url)
					.then(response => response.json())
					.then(jsonData => {	
						series = jsonData.content;
						if(totalPages != jsonData.totalPages){
							totalPages = jsonData.totalPages;
						}
					});
			query = false;
		}
	});

	afterUpdate(() => {
		currentPage = page;
	});

	function handlePage(number){
		page = page + number;
		query = true;
	}

	function handlePageSize(number){
		params.pageSize = number;
		page = 0;
		series = [];
		query = true;
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

	function handleFilter(){
		page = 0;
		series = [];
		query = true;
	}
	
</script>

{#if !excel && !grafica}
<Paper>
	<Title>Listado de series</Title>
	
		<div class="columns margins" style="justify-content: flex-start;" >
		<div>
		<Textfield bind:value={params.keyword} label="Palabra clave" />
		</div>

		<div>
		<Select variant="filled" enhanced bind:value={params.origen} label="Origen">
			<Option value=""></Option>
			{#each filterOptions.origen as origen}
				<Option value={origen} selected={params.origen === origen}>{origen}</Option>
			{/each}
		</Select>
		</div>

		<div>
		<Select variant="filled" enhanced bind:value={params.fuente} label="Fuente">
			<Option value=""></Option>
			{#each filterOptions.fuente as fuente}
				<Option value={fuente} selected={params.fuente === fuente}>{fuente}</Option>
			{/each}
		</Select>
		</div>

		<div>
		<Select variant="filled" enhanced bind:value={params.periodicidad} label="Periodicidad">
			<Option value=""></Option>
			{#each filterOptions.periodicidad as periodo}
				<Option value={periodo} selected={() => params.periodicidad === periodo}>{periodo}</Option>
			{/each}
		</Select>
		</div>

		<div>
		<Select variant="filled" enhanced bind:value={params.territorio} label="Territorio">
			<Option value=""></Option>
			{#each filterOptions.territorio as territorio}
				<Option value={territorio} selected={params.territorio === territorio}>{territorio}</Option>
			{/each}
		</Select>
		</div>
		
		<Button variant="outlined" on:click={() => handleFilter()} >
			Filtrar
		</Button>
		</div>

	<div class="table">
		<DataTable table$aria-label="Series">
			<Head>
				<Row>
					<Cell><strong>Código</strong></Cell>
					<Cell><strong>Descripción</strong></Cell>
					<Cell><strong>Fuente</strong></Cell>
					<Cell><strong>Nº de Datos</strong></Cell>
					<Cell><strong>Periodicidad</strong></Cell>
					<Cell><strong>Datos</strong></Cell>
					<Cell><strong>Territorio</strong></Cell>
					<Cell><strong>Selección</strong></Cell>
				</Row>
			</Head>
			<Body>
				{#if series.length}
					{#each series as serie, i (i)}
						<Row>
							<Cell>{serie.codigo}</Cell>
							<Cell>{serie.descripcion.toLowerCase()}</Cell>

							{#if serie.nombreFuente}
								<Cell>{serie.nombreFuente} ({serie.siglasFuente})</Cell>
							{:else}
								<Cell>{serie.siglasFuente}</Cell>
							{/if}

							{#if serie.datos}
								<Cell>{serie.datos.length}</Cell>
							{:else}
								<Cell>0</Cell>
							{/if}

							<Cell>{serie.periodicidad}</Cell>

							{#if serie.tasa}
								<Cell>{serie.tasa} ({serie.unidad.toLowerCase()})</Cell>
							{:else}
								<Cell>{serie.unidad.toLowerCase()}</Cell>
							{/if}

							<Cell>{serie.territorio}</Cell>

							<Cell checkbox>
								<Checkbox on:click={() => handleSelect(serie)} checked={isSelected(serie)}/>
							</Cell>
						</Row>
					{/each}
				{:else}
					<Row>
						<Cell><h3>No se encontraron resultados</h3></Cell>
					</Row>
				{/if}
			</Body>
		</DataTable>
	</div>
	<div class="margins">
		<Select enhanced bind:value={params.pageSize} label="Numero de series">
  			{#each sizes as size}
   				<Option value={size} selected={params.pageSize === size} on:click={() => handlePageSize(size)}>{size}</Option>
  			{/each}
		</Select>
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
	</div>

	<div class="margins">
	
	<Button variant="raised" on:click={() => excel = true} disabled={selected.length < 1 }>
		Ver datos en Excel
	</Button>
	
	<Button variant="raised" on:click={() => grafica = true} disabled={selected.length < 1 }>
		Ver datos en Gráfica
	</Button>
	</div>

	<div class="margins">


	<Set chips={selected} let:chip input>
  		<Chip><Text>{chip.codigo}</Text></Chip>
	</Set>

	</div>
	
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

<style>
  .margins {
    margin: 18px 0 24px;
  }
  .columns {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
  }
  .columns > * {
    margin-left: 12px;
  }
  .columns > *:first-child {
    margin-left: 0;
  }
</style>