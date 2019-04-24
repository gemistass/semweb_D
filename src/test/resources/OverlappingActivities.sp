PREFIX sa:<http://www.sensoractivities.org/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
select ?act1 ?start1 ?end1 ?act2 ?start2 ?end2 where {
    ?act1 rdf:type sa:activity;
          sa:start ?start1;
          sa:end ?end1.
    ?act2 rdf:type sa:activity;
          sa:start ?start2;
          sa:end ?end2.
    filter(( ?start1<=?start2 && ?start2<?end1 && ?end1<?end2)|| (?start2<=?start1 && ?start1<?end2 && ?end2<?end1 )  )  .
    filter(?act1 != ?act2)
}