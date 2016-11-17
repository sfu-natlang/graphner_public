#include<iostream>
#include<fstream>
#include<unordered_map>
#include<vector>
#include<sstream>
#include<algorithm>
#include<iterator>

using namespace std;

void Tokenize(string line, vector<string>& tokens, string delimiter=" ")
{
	for (int i=0; i < delimiter.size(); i++)
		replace(line.begin(), line.end(),delimiter[i],' ');
	stringstream ss(line);
	copy(istream_iterator<string>(ss), istream_iterator<string>(), back_inserter(tokens));
}


int main(int argc, char** argv)
{
	if (argc!= 6)
	{
		cout << "Usage: executabel.o 0 DistributionFile OutputFile n(as in ngram) L(the number of labels) \n" << endl;
		return 1;
	}

	int n = atoi(argv[4]);
	int L = atoi(argv[5]);

	unordered_map<string, int> count;
	unordered_map<string, vector<double>> sums;

	ifstream inf;
	inf.open(argv[2]);

	string line;
	while (getline(inf,line).good())
	{
		vector<string> tokens;
		Tokenize(line, tokens, " \t");
		if (tokens.size()==0)
			continue;
		else if (tokens.size()<n+L)
			cout << "error: too few tokens in line " << line << endl;
		else
		{
			string ngram = "";
			for (int i=0; i<n; i++)
				ngram+= tokens[i]+" ";
			if (sums[ngram].size()==0)
			{
				for (int l=0; l<L; l++)
					sums[ngram].push_back(atof(tokens[l+n].c_str()));
			}
			else
			{
				for (int l=0; l<L; l++)
                                        sums[ngram][l]+=(atof(tokens[l+n].c_str()));
			}
			count[ngram]++;
		}
	}
		
	inf.close();

	ofstream outf;
	outf.open(argv[3]);

	for (auto v=sums.begin(); v!= sums.end(); v++)
	{
		outf<< v->first << "\t";
		int c = count[v->first];
		for (auto element = (v->second).begin();  element != (v->second).end(); element++)
			outf<< (*element)/((float)c)<<"\t";
		outf<< endl;
	}	

	outf.close();

	return 0;
}
