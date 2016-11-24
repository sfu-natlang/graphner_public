cp ../Data/Graphs/$YOURDATAVertexMapFile ../Pipeline/MiddleFiles/BCGM_VertexMap.txt
cp ../Data/Graphs/$YOURDATAGraphStructureFile ../Pipeline/MiddleFiles/BCGM_GraphStructure.txt

cd ../Pipeline

./Experiment_on_new_DataSet.sh  $alpha  $mu $nu $iterationNumber  config/$train_YOURDATA_inBANNER.xml config/$train_YOURDATA_inBANNER.xml ../../$YourDATA/test/GENE.eval ../../$YourDATA/test/ALTGENE.eval
