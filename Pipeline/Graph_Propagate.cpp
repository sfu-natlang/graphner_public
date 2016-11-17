#include <iostream>
#include <fstream>
#include <stdlib.h>
#include <vector>
#include <set>
#include <map>
#include <sstream>
#include <iterator>
#include <string.h>
#include <algorithm>

using namespace std;

vector<vector<float>> q0;
vector<vector<float>> q1;
vector<vector<float>> ref;
vector<vector<int>>  N;
vector<vector<float>> w;
map<string,int> VertexMap;
set<int> Labelled_Vertices;

int VSize; // number of vertices
int TSize; // number of tags

void Tokenize(string line, vector<string>& tokens , string delimiter=" ")
{	
	for (int i=0; i<(int)delimiter.size(); i++)
	{		 
		replace(line.begin(), line.end(), delimiter[i],' ');
	}
	
        stringstream iss(line);
        copy(istream_iterator<string>(iss), istream_iterator<string>(), back_inserter(tokens));
}

void Read_Graph(string GraphFilePath)
{
	ifstream f_Graph;
	f_Graph.open(GraphFilePath.c_str());

	// initializing v and w
	vector<int> v;
	N.assign(VSize,v);
	vector<float> v2;
	w.assign(VSize,v2);
	
	string line;
	while(f_Graph.good())
	{
		getline(f_Graph,line);
		vector<string> tokens;
		Tokenize(line, tokens, ":");
		int size = (int) tokens.size();
		if (size ==0)
			continue;
		/*cout<<"in line "<<line<<"\ntokens:\n";
                        for (int i=0; i<size; i++)
                                cout<<tokens[i]<<"\t";
		cout<<"\nsize:"<<size<<endl;*/
		if (size%2!=1)
		{
			cout<<"error in GraphFile, size="<<size<<"in line"<<line<<"\ntokens:\n";
			for (int i=0; i<size; i++)
				cout<<tokens[i]<<"\t";			
			throw 1;
		}
		int u = atoi(tokens[0].c_str());
		int n = (size - 1 )/2; // was'nt it better if we chose another name here? like maybe k or k_prime
		//cout<<"n: "<<n<<endl;

		for (int i=1; i<=n; i++)
		{
			int v = atoi(tokens[2*i-1].c_str());
			N[u].push_back(v);
			w[u].push_back(atof(tokens[2*i].c_str()));
		}
	}

	f_Graph.close();
}

void Read_Vertices(string VerticesFilePath)
{
	ifstream f_Vertices;
	f_Vertices.open(VerticesFilePath);

	VSize = 0;

	string line;
	while (f_Vertices.good())
	{
		getline(f_Vertices, line);
		vector<string> tokens;
		Tokenize(line, tokens, " \t");
		int tokensSize = (int) tokens.size();
		if (tokensSize==0)
			continue;
		int n = tokensSize -1;
		//if (tokensSize != 2)
		//	cout<<"error\n";
		int u =atoi(tokens[0].c_str());
		string ngram = "";
		for (int i=0; i<n; i++)
			ngram +=(tokens[i+1]+" ");
		VertexMap[ngram]= u;
		if (u>=VSize)
			VSize = u+1;
	}	
	f_Vertices.close();
}

void Read_Dists(string RawDistFilePath, string RefFilePath, int n)
{
	ifstream f_q, f_Ref;
	f_q.open(RawDistFilePath.c_str());	

	string line;
	TSize = 0;
	while (f_q.good())
	{
		getline(f_q, line);
                vector<string> tokens;
                Tokenize(line, tokens," \t");

		int tokensSize = (int) tokens.size();
		if (tokensSize==0)
			continue;
		if (TSize==0)
		{
			TSize = tokensSize -n;
			
			// initialization
			vector<float> tVec(TSize);
			q0.assign(VSize, tVec);
			q1.assign(VSize, tVec);
			ref.assign(VSize,tVec);
			
		}
		else if (TSize != tokensSize -n)
		{
			cout<<"error in number of tags\n";
			throw 2;
		}

		string ngram= "";
		for (int i=0; i<n; i++)
			ngram += (tokens[i]+" ");		

		if (VertexMap.find(ngram)!=VertexMap.end())
		{
			int u=VertexMap[ngram];
			//cout << ngram << " is not in references and is mapped to " << u << endl;
			for (int t=0; t<TSize; t++)
			{
				q0[u][t]=atof(tokens[t+n].c_str()); 
				q1[u][t]=q0[u][t];
			}
		}
		/*else
                        cout << ngram << " is not in references and it's not in the graph vertices either. " << endl;*/

	}
	f_q.close();	

        f_Ref.open(RefFilePath.c_str());
	while (f_Ref.good())
        {
                getline(f_Ref, line);
                vector<string> tokens;
                Tokenize(line, tokens, " \t");

                int tokensSize = (int) tokens.size();
		if (tokensSize==0)
			continue;
                if (TSize != tokensSize -n)
                {
                        cout<<"error in number of ref-tags\n";
                        throw 3;
                }

                string ngram= "";       
                for (int i=0; i<n; i++)
                        ngram += (tokens[i]+" ");       

		if (VertexMap.find(ngram)!=VertexMap.end())
		{
	                int u=VertexMap[ngram];		
			//cout << ngram << " is in references and is mapped to " << u << endl;
			Labelled_Vertices.insert(u);
	                for (int t=0; t<TSize; t++)
                	        ref[u][t]=atof(tokens[t+n].c_str()); 
		}
		/*else
			cout << ngram << " is in references but is not in the graph vertices. " << endl;*/
        }	
	f_Ref.close();
}

vector<vector<float>> * Propagate(int ItrNum, float mu, float nu)
{
	
	vector<vector<float>> * q_prv = & q0;
	vector<vector<float>> * q = &q1;
	vector<float> k;
	k.reserve(VSize);
	vector<float> mu_over_k;
	mu_over_k.reserve(VSize);
	vector<vector<float>> r_plus_nu_Unified_over_k(VSize);	

	cout << "VSize :" << VSize << endl;
	for (int u=0; u<VSize; u++)
	{
		if (Labelled_Vertices.find(u)!=Labelled_Vertices.cend())
			k.push_back(nu+1);
		else
			k.push_back(nu);
	
		for (int i=0; i<(int)N[u].size(); i++)			
			k[u]+= mu*w[u][i]; // note: w[u] is a vector of size N[u] and w[u][i] is the weight of edge between u and v=N[u][i]			
		if (k[u]==0)
		{
			cout<<"k["<<u<<"]=0 error!\n";
			throw 4;
		}		
		mu_over_k.push_back(mu/k[u]);
		
		r_plus_nu_Unified_over_k[u].assign(TSize,0);		
		float nunif = (float)nu/(float)TSize;
		for (int t=0; t<TSize; t++)
		{
			if (Labelled_Vertices.find(u)!=Labelled_Vertices.cend())
				r_plus_nu_Unified_over_k[u][t]= (ref[u][t]+nunif)/k[u];
			else
				r_plus_nu_Unified_over_k[u][t] = nunif/k[u];					
		}
	}

	for (int itr = 0; itr<ItrNum; itr++)
	{
		vector<vector<float>> * tmp = q_prv;
                q_prv = q;
                q = tmp;

		for (int u=0; u<VSize; u++)
		{
			for (int t=0; t<TSize; t++)
			{
				(*q)[u][t]=0;
				for (int i=0; i<(int)N[u].size(); i++)
				{
					int v =N[u][i];
					(*q)[u][t] += mu_over_k[u]*w[u][i]*(*q_prv)[v][t];
				}
				(*q)[u][t]+= r_plus_nu_Unified_over_k[u][t];				
			}			
		}		
	}
	return q; 
}

void PrintOutput(string OutputPath,string RawDistFilePath, vector<vector<float>> * q, int n) 
// Note: This function needs RawDistFilePath because 1. it produces the smoothed Dists in the same order; 2. it outputs Rawdist for ngrams absent from the graph.
{
	ifstream f_q;
	ofstream outf;
	f_q.open(RawDistFilePath.c_str());
	outf.open(OutputPath.c_str());

	string line;
	while(f_q.good())
	{
		getline(f_q, line);
                vector<string> tokens;
                Tokenize(line, tokens, " \t");
		if ((int)tokens.size()==0)
			continue;

		string ngram= "";       
                for (int i=0; i<n; i++)
                        ngram += (tokens[i]+" ");       

		
                int u;
		if (VertexMap.find(ngram)!= VertexMap.end())
		{
			u =VertexMap[ngram]; 
			outf<< ngram <<"\t";
			for (int t=0; t<TSize; t++)
				outf<<(*q)[u][t]<<"\t";
			outf<<endl;		
		}
		else
			outf << line << endl; //We just output the RawDist for and ngram that is not present in the graph. 
	}	
	f_q.close();
	outf.close();
}

void Test_Graph_Read()
{
	ofstream outf;
	outf.open("Test_Graph_Read.out");

	outf<< "VSize: "<< VSize << endl;
	outf<< "\n\nV_l: {";
	for (auto it=Labelled_Vertices.cbegin(); it!= Labelled_Vertices.cend(); ++it)
		outf<<*it<<", ";
	outf<<"}\n\n";

	outf<< "\nVertexMap:{\n";
	for (auto it=VertexMap.cbegin(); it!=VertexMap.cend(); ++it)
		outf<<"("<<it->second<<":"<<it->first<<"), ";
	outf<<"}\n\n";

	outf<<"N,w:\n";
	for (int i=0; i<VSize; i++)
	{
		outf<<i<<":\t";
		for (int j=0; j<(int)N[i].size(); j++)
		{
			outf<< N[i][j]<<":"<<w[i][j] <<"\t\t";
		}
		outf<<endl; 
	}	
	outf<<"-----------------------------\n\n";

	outf.close();
}

const std::string currentDateTime() {
    time_t     now = time(0);
    struct tm  tstruct;
    char       buf[80];
    tstruct = *localtime(&now);
    // Visit http://en.cppreference.com/w/cpp/chrono/c/strftime
    // for more information about date/time format
    strftime(buf, sizeof(buf), "%Y-%m-%d.%X", &tstruct);
    
    return buf;
}
                 

int main(int argc, char** argv)
{
	if ((argc!=11)&&(argc!=12))
	{
		cout << "Usage: ./Graph_Propagate.o 0 GraphFilePath VerticesFilePath RawDistributionsFilePath ReferenceDistributionsFilePath ItrNum mu nu OutputFilePath n(as in ngram vertex) D(an optional number that mu and nu gets divided by it) \n";
		return 1;
	}
	float D=1.0;
	if (argc==12)
		D=atof(argv[11]);

	int n = atoi(argv[10]);

	std::cout << "currentDateTime()=" << currentDateTime() << std::endl;
	// Read Data
	Read_Vertices(argv[3]);
	
	Read_Dists(argv[4],argv[5],n); // needs to be done after the number of vertices is read in previous function
	Read_Graph(argv[2]); // needs to be done after the number of vertices and the number of tags are read in previous functions	

	/*
 	// Test if the data was read correctly.
	Test_Graph_Read();	
	vector<vector<float>> * tmp;
	tmp = &q0;
	PrintOutput("Test_Read_q0.out",argv[4], tmp);
	tmp = &q1;
        PrintOutput("Test_Read_q1.out",argv[4], tmp);
	tmp = &ref;
        PrintOutput("Test_Read_ref.out",argv[5], tmp);
	*/

	// Propagate and output
	float mu = atof(argv[7])/D;
	float nu = atof(argv[8])/D;

	cout << "mu: " << mu << " nu: "<< nu <<endl;	
	vector<vector<float>> * q = Propagate(atoi(argv[6]),mu,nu);
	PrintOutput(argv[9],argv[4], q, n);
	std::cout << "currentDateTime()=" << currentDateTime() << std::endl;

	return 0;
}
