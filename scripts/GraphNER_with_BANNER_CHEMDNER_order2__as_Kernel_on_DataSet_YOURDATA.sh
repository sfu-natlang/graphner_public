cp ../Data/Graphs/$YOURDATAVertexMapFile ../Pipeline/MiddleFiles/BCGM_VertexMap.txt
cp ../Data/Graphs/$YOURDATAGraphStructureFile ../Pipeline/MiddleFiles/BCGM_GraphStructure.txt

cd ../Pipeline

./banner_Experiment_on_new_DataSet.sh  $alpha  $mu $nu $iterationNumber  config/$train_YOURDATA_inBANNER_ChemDNER.xml config/$train_YOURDATA_inBANNER_ChemDNER.xml ../../$YourDATA/test/GENE.eval ../../$YourDATA/test/ALTGENE.eval
