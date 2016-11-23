cp ../Data/Graphs/AML_AllFeatures_LesThan10000_VertexMap.txt ../Pipeline/MiddleFiles/BCGM_VertexMap.txt
cp ../Data/Graphs/AML_AllFeatures_LesThan10000_GraphStructure.txt ../Pipeline/MiddleFiles/BCGM_GraphStructure.txt

cd ../Pipeline

./Experiment_on_new_DataSet.sh  2  1 1 2  config/banner_order2_AML_train_BC2GM_Format.xml config/banner_order2_AML_test_BC2GM_Format.xml ../../AML_geneMention__in_bc2geneMentionFormat/test/GENE.eval ../../AML_geneMention__in_bc2geneMentionFormat/test/ALTGENE.eval
