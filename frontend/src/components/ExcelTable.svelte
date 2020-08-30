<script>
	import { onMount, beforeUpdate } from 'svelte';
	import DataTable, {Head, Body, Row, Cell} from '@smui/data-table';
	import Paper, {Title, Subtitle, Content} from '@smui/paper';
	import { csvGenerator } from "./csvGenerator";
	import Button, { Label } from '@smui/button';
    
    export let selected;
	let data = [];
	let loading = true;

	beforeUpdate(async function() {
        if(selected.length > 0) {
			for(const serie of selected){
				for(const dato of serie.datos){
					var result = data.find(obj => {
						return obj.fecha === dato.anio + " " +dato.periodo
					})
					const id = serie.id;
					const valor = dato.valor;
					
					if(result) {
						result.valores.push(valor);
					} else {
						let valores = [];
						for (var n = 0; n < selected.indexOf(serie); n++) {
							valores.push("-");
						}
						valores.push(valor);
						data.push({
							"fecha": dato.anio + " " +dato.periodo,
							"territorio": serie.territorio,
							"valores": valores
							})

					}
				}
			};
			for(const dato of data){
				while (dato.valores.length < selected.length) {
					dato.valores.push("-");
				};
			};
			data.sort((a,b) => (a.fecha > b.fecha) ? 1 : ((b.fecha > a.fecha) ? -1 : 0)); 
			loading = false;
        }
	});

	function downloadHandler() {
		let tableData = [];
		let tableKeys = ['Fecha']
		for(const serie of selected){
			tableKeys.push(serie.descripcion)
		}
		for(const item of data) {
			var row = {"Fecha": item.fecha}
			for(const serie of selected){
				row[serie.descripcion] = item.valores[selected.indexOf(serie)]
			}
			tableData.push(row)
		}
		
		csvGenerator(tableData, "tableHeader", true);
	}
</script>

{#if loading}
loading...
{:else}
	<Paper>
		<Title>Datos</Title>
		<Content>
			<Button variant="outlined" on:click={downloadHandler}>
				<Label>Descargar Excel</Label>
			</Button>
		</Content>
		<Content>
			<DataTable table$aria-label="Series" >
				<Head>
					<Row>
						<Cell>Fecha</Cell>
						<!-- <Cell>Territorio</Cell> -->
						{#each selected as serie, i (i)}
							<Cell>{serie.descripcion}</Cell>
						{/each}
					</Row>
				</Head>
				<Body>
				{#each data as dato, i (i)}
					<Row>
						<Cell>{dato.fecha}</Cell>
						<!-- <Cell>{dato.territorio}</Cell>	 -->
						{#each dato.valores as valor, i (i)}
							<Cell>{valor}</Cell>
						{/each}
					</Row>
					{/each}
				</Body>
			</DataTable>
		</Content>
		<Content>
			
			<Button variant="outlined" on:click={downloadHandler}>
				<Label>Descargar Excel</Label>
			</Button>
		</Content>
	</Paper>
{/if}