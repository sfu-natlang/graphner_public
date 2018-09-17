#include <fstream>
#include <iostream>
#include <unordered_map>
#include <vector>
#include<iterator>
#include<algorithm>
#include<sstream>
#include<set>

using namespace std;

unordered_map<string,int> FPs, FNs, TPs;
set<string> Annotated_SentenceIds, Test_SentenceIds;

void Tokenize(string line, vector<string>& tokens, string delimiter=" \t")
{
        for (int i=0; i<delimiter.size(); i++)
                replace(line.begin(), line.end(), delimiter[i], ' ');

        stringstream ss(line);
        copy(istream_iterator<string>(ss), istream_iterator<string>(), back_inserter(tokens));
}


void read_TP_FP_FNs(const char* infName)
{
	ifstream inf;
	inf.open(infName);

	int FN_num=0;
	string line;
	while (getline(inf, line))
	{
		vector<string> tokens;
		Tokenize(line, tokens, "|");
		if (tokens.size()<2)
			continue;
		else
		{
			Annotated_SentenceIds.insert(tokens[1]);
			if (tokens[0]=="FN"){
				FN_num++;
				FNs[tokens[1]]++;
			}
			else if (tokens[0]=="FP")
				FPs[tokens[1]]++;
			else //some kind of TP
				TPs[tokens[1]]++;
		}
	}

	//cout << "FN_num:" << FN_num << endl;
	inf.close();
}



void makeOutput(const char* infile, const char* outfile)
{
	ifstream inf;
	inf.open(infile);
	ofstream outf;
	outf.open(outfile);

	int Used_FN_num=0; 

	string line;
	while (getline(inf,line))
	{
		vector<string> tokens;
		Tokenize(line, tokens, " \t");
		if (tokens.size()<1)
			continue;
		else{
			Test_SentenceIds.insert(tokens[0]);			
			outf << TPs[tokens[0]] << " " << TPs[tokens[0]]+FPs[tokens[0]] <<" " << TPs[tokens[0]]+FNs[tokens[0]] << endl;
			Used_FN_num +=FNs[tokens[0]]; 
		}
	}
	outf.close();
	inf.close();
	//cout << "Used_FN_num:" << Used_FN_num << endl; 
}


int main(int argc, char** argv)
{

	if (argc != 5)
	{
		cout << "Usage: executable.o 0 test.inFilePath TP_FP_FN_FilePath(output of alt_eval_GolnarVersion.perl) outputPath(InputOf_sigf)  \n";
		return 1;
	}

	read_TP_FP_FNs(argv[3]);
	makeOutput(argv[2],argv[4]);

	/*for (auto itr=Annotated_SentenceIds.begin(); itr!=Annotated_SentenceIds.end(); itr++){
		if (Test_SentenceIds.find(*itr)==Test_SentenceIds.end())
			cout << "Non-existant annotated Sentence Id:" << *itr << endl;
	}*/
	return 0;

}
