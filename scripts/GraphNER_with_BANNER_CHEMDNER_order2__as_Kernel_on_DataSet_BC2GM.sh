cp ../Data/Graphs/BCGM_VertexMap_simplisticFeatures_TrainAndTest.txt ../Pipeline/MiddleFiles/BCGM_VertexMap.txt
cp ../Data/Graphs/BCGM_GraphStructure_simplisticFeatures_TrainAndTest.txt ../Pipeline/MiddleFiles/BCGM_GraphStructure.txt

cd ../Pipeline

./banner_chemdner_Experiment_on_new_DataSet.sh  2  1 1 3  config/banner_order2_BC2GM_brown1000pubmed_simple_tokenized_WVC500_WST_prod.xml config/banner_order2_BC2GM_brown1000pubmed_simple_tokenized_WVC500_WST_test_prod.xml ../test/GENE.eval ../test/ALTGENE.eval

