<script>
	import { onMount, beforeUpdate } from 'svelte';
	import DataTable, {Head, Body, Row, Cell} from '@smui/data-table';
	import Paper, {Title, Subtitle, Content} from '@smui/paper';
    
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
						console.log(selected.indexOf(serie));
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
</script>

{#if loading}
loading...
{:else}
	<Paper>
		<Title>Datos</Title>
		<Content>
			<DataTable table$aria-label="Series" >
				<Head>
					<Row>
						<Cell>Fecha</Cell>
						<!-- <Cell>Territorio</Cell> -->
						{#each selected as serie, i (i)}
							<Cell>{serie.descripcion}</Cell>
						{/each}
						
						{#if selected.length == 2}
							<Cell>Diferencia</Cell>
						{/if}
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
						{#if dato.valores.length == 2}
							<Cell>{dato.valores[0] - dato.valores[1]}</Cell>
						{/if}
					</Row>
					{/each}
				</Body>
			</DataTable>
		</Content>
	</Paper>
{/if}