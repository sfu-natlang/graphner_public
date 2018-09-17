test_Input_FilePath=../Data/AML_geneMention__in_bc2geneMentionFormat/test/test.in
DetailedOutput1=../DetailedOutputs/GraphNer_KernelBANNERChemDNER_on_ALM_Iteration1_outputmentions
DetailedOutput2=../DetailedOutputs/GraphNer_KernelBANNERChemDNER_on_ALM_Iteration2_outputmentions

./make_sigf_Input.o 0 $test_Input_FilePath $DetailedOutput1 sigf/exampleFScore/model_1
./make_sigf_Input.o 0 $test_Input_FilePath $DetailedOutput2 sigf/exampleFScore/model_2

cd sigf/class
java de/pado/sigf/FScoreART ../exampleFScore/model_1 ../exampleFScore/model_2  10000

