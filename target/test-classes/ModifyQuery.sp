PREFIX sa:<http://www.sensoractivities.org/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX : <http://www.w3.org/2000/01/rdf-schema#>
DELETE {
    ?act2 sa:end ?end2 .
    ?act1a sa:end ?end1a .
}	
INSERT {
    ?act2 sa:end ?start1.
    ?act1a sa:end ?start2a
}WHERE {
    ?act1 rdf:type sa:activity;
          sa:end ?end1;
          sa:start ?start1.
    ?act2 sa:start ?start2;
          rdf:type sa:activity;
          sa:end ?end2.
    ?act1a rdf:type sa:activity;
           sa:end ?end1a;
           sa:start ?start1a.
    ?act2a sa:start ?start2a;
           rdf:type sa:activity;
           sa:end ?end2a.
    filter((?start2<=?start1 && ?start1<?end2 && ?end2<?end1 &&?act1 != ?act2)) .
    filter(( ?start1a<=?start2a && ?start2a<?end1a && ?end1a<?end2a &&?act1a != ?act2a)) .
}