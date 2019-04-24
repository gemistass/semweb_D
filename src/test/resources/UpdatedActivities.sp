PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX sa:<http://www.sensoractivities.org/>
PREFIX : <http://www.w3.org/2000/01/rdf-schema#>

select ?s ?start ?end where { 
	?s rdf:type sa:activity .
    ?s sa:end ?end;
       sa:start ?start
} 
