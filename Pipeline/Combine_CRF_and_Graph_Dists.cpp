#include <iostream>
#include <fstream>
#include <vector>
#include <unordered_map>
#include <iterator>
#include <sstream>
#include <algorithm>

using namespace std;

void Tokenize(string line, vector<string>& tokens, string delimiter=" \t")
{
	for (int i=0; i<delimiter.size(); i++)
		replace(line.begin(), line.end(), delimiter[i], ' ');
	stringstream ss(line);
	copy(istream_iterator<string>(ss), istream_iterator<string>(), back_inserter(tokens));
}

unordered_map<string, vector<double>> Graph_Dist;

int Read_Graph_Dists(char* GraphDistsFilePath, int n, int L)
{
	ifstream inf; 
	inf.open(GraphDistsFilePath);

	string line;
	while (getline(inf, line).good())
	{
		vector<string> tokens;
		Tokenize(line, tokens, " \t");
		if (tokens.size()!= n+L)
		{
			cout << "incorrect number of tokens in Graph Dist : " << line << " with " << tokens.size() << " tokens." << endl;
			return 1;	
		}
		else
		{
			string ngram = "";
			for (int i=0; i<n; i++)
				ngram += tokens[i] + " ";

			vector<double>* GD = &(Graph_Dist[ngram]);
			if (GD->size()!=0)
			{
				cout << "repeated ngram in Graph Dists : " << ngram << endl;
				return 1;
			}
			for (int l=0; l<n; l++)			
				GD->push_back(atof(tokens[n+l].c_str()));
		}
	}

	inf.close();
	return 0;
}

int main(int argc, char** argv)
{
	if (argc!=8)
	{
		cout <<"Usage: executable.o 0 CRF_marginals Graph_Dists OutputFilePath alpha(real number between 0 and 1 or integer between 2 and 100) n(as in ngram) L(number of labels) \n";
		return 1;
	}

	float alpha = atof(argv[5]);
	if (alpha > 1)
		alpha /=100;
	cout << alpha << endl;
	int n = atoi(argv[6]);
	int L = atoi(argv[7]);

	if (Read_Graph_Dists(argv[3],n,L)==1)
		return 1;

	ifstream CRF_f;
	CRF_f.open(argv[2]);
	ofstream outf;
	outf.open(argv[4]);
	string CRF_line;
	while (getline(CRF_f, CRF_line))
	{
		vector<string> tokens;
		Tokenize(CRF_line, tokens, " \t");
		if (tokens.size()==0)
		{
			outf << endl;
			continue;	
		}
		if (tokens.size()!= 2*n+L)
		{
			cout << "bad number of tokens in CRF_line " << CRF_line << endl;
			return 1;
		}
		else
		{
			string ngram = "";
			for (int i=0; i<n; i++)
				ngram += tokens[i]+" ";

			outf << ngram << "\t";
			if (Graph_Dist.find(ngram)==Graph_Dist.end())
			{
				cout << ngram << " not found in the graph! " << endl;
				outf << endl;
				continue;
			}
			vector<double> * GD = &Graph_Dist[ngram];

			for (int l=0; l<L; l++)
				outf << alpha*(atof(tokens[n+l].c_str()))+ (1-alpha)* ((*GD)[l]) << "\t"; // if time complexity is an issue, we can save a little bit of time by saving the (1-alpha)* Graph Dist and not multiplying every time
			for (int i=0; i<n; i++)
				outf << tokens[n+L+i]<<" ";
			outf << endl;
		}
	}
	outf.close();
	CRF_f.close();
	

	return 0;
}
