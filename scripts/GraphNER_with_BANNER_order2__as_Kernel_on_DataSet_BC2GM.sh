cp ../Data/Graphs/BCGM_VertexMap_simplisticFeatures_TrainAndTest.txt ../Pipeline/MiddleFiles/BCGM_VertexMap.txt
cp ../Data/Graphs/BCGM_GraphStructure_simplisticFeatures_TrainAndTest.txt ../Pipeline/MiddleFiles/BCGM_GraphStructure.txt

cd ../Pipeline

./Experiment_on_new_DataSet.sh  2  1 100 2  config/banner_order2_BC2GM.xml config/banner_order2_BC2GM_TEST.xml ../test/GENE.eval ../test/ALTGENE.eval
