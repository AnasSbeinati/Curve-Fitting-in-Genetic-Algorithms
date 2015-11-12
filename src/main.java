import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class main {

	public static void main(String[] args) {
		FileReader fr;
		BufferedReader br = null;
		FileWriter fw;
		BufferedWriter bw = null;
		String newline = System.getProperty("line.separator");
		try {
			fr = new FileReader("inputSample.txt");
			br = new BufferedReader(fr);
			fw = new FileWriter("input_exp_answer.txt");
			bw = new BufferedWriter(fw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int M = 0;
		try {
			M = Integer.parseInt(br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < M; i++) {
			try {
				String strN = "";
				String strD = "";
				int N;
				int D;
				Input input = new Input();
				Output output;
				ArrayList<FloatPoint> points = new ArrayList<>();
				while (strN.equals("")) {
					strN = br.readLine();
				}
				String cStr[] = strN.split(" ");
				N = Integer.parseInt(cStr[0]);
				D = Integer.parseInt(cStr[1]);
				for (int j = 0; j < N; j++) {
					FloatPoint point = new FloatPoint();
					String str = br.readLine();
					if (!str.equals("")) {
						String strs[] = str.split(" ");
						point.x = Float.valueOf(strs[0]);
						point.y = Float.valueOf(strs[1]);
					}
					points.add(point);
				}
				input.equationDegree = D;
				input.iterationsNum = 500;
				input.numOfPoints = N;
				input.populationSize = 1000;
				input.points = points;
				output = getSol(input);
				System.out.println("Case: " + (i + 1) + " :");
				bw.write("Case: " + (i + 1) + " " + " :\n");
				int j=0;
				for(Float floatC:output.coffecient) {
					System.out.print((floatC>=0?(" +"+floatC):floatC)+"*X^"+j+" ");
					bw.write((floatC>=0?(" +"+floatC):floatC)+"*X^"+j+" ");
					j++;
				}
				System.out.println();
				bw.write("\n");
				System.out.println("Error is : "+ output.error);
				bw.write("Error is : "+ output.error+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Output getSol(Input input) {
		// generate population
		ArrayList<Chromosome> population = generatePop(input);
		replace(input, population);
		// fitness the first time
		fitness(input, population);
		for (int i = 0; i < input.iterationsNum; i++) {
			for (int j = 0; j < input.populationSize / 2; j++) {
				// select
				ArrayList<Chromosome> towChrom = select(input, population);
				// crossOver
				crossover(input, towChrom);
				// mutation
				mutate(input, towChrom, i + 1);
				// put new sols
				for (int k = 0; k < towChrom.size(); k++) {
					if (towChrom.get(k).isChanged) {
						towChrom.get(k).isChanged = false;
						population.add(towChrom.get(k));
					}
				}
			}
			// Create the new population
			replace(input, population);
		}
		// create output
		Output output = new Output();
		output.coffecient = population.get(0).genes;
		output.error = population.get(0).fitness;
		return output;
	}

	private static void replace(Input input, ArrayList<Chromosome> population) {
		Collections.sort(population, new Comparator<Chromosome>() {

			@Override
			public int compare(Chromosome ch1, Chromosome ch2) {
				if (ch1.inversFitness > ch2.inversFitness)
					return -1;
				else if (ch1.inversFitness < ch2.inversFitness)
					return 1;
				else
					return 0;
			}
		});
		ArrayList<Chromosome> temp = new ArrayList<>(population.subList(0, input.populationSize));
		population.removeAll(population);
		population.addAll(temp);
	}

	private static void mutate(Input input, ArrayList<Chromosome> towChrom, int currentIteration) {
		for (int i = 0; i < 2; i++) {
			//if (r < input.mutationConst) {
				float po = (float) Math.pow(1 - currentIteration / input.iterationsNum, input.equationDegree);
				for (Float gen : towChrom.get(i).genes) {
					float r1 = (float) Math.random();
					float mut = 1 - (float) Math.pow(r1, po);
					float y = (float) Math.random();
					float d=y*mut;
					if(y<.5)
						gen -= mut;
					else
						gen += mut;
				    towChrom.get(i).isChanged = true;
			}
		}
	}

	private static void crossover(Input input, ArrayList<Chromosome> towChrom) {
		float r = (float) Math.random();
		int i = 1 + (int) (Math.random() * (input.equationDegree));
		if (r < input.crossOverConst) {
			ArrayList<Float> temp1 = new ArrayList<>(towChrom.get(0).genes.subList(0, i));
			ArrayList<Float> temp2 = new ArrayList<>(towChrom.get(1).genes.subList(0, i));
			ArrayList<Float> temp3 = new ArrayList<>(towChrom.get(0).genes.subList(i, input.equationDegree + 1));
			ArrayList<Float> temp4 = new ArrayList<>(towChrom.get(1).genes.subList(i, input.equationDegree + 1));
			temp1.addAll(temp4);
			temp2.addAll(temp3);
			towChrom.get(0).genes = null;
			towChrom.get(1).genes = null;
			towChrom.get(0).genes = temp1;
			towChrom.get(1).genes = temp2;
			towChrom.get(0).isChanged = true;
			towChrom.get(1).isChanged = true;
		}

	}

	private static ArrayList<Chromosome> select(Input input, ArrayList<Chromosome> population) {
		ArrayList<Chromosome> towChrom = new ArrayList<>();
		Collections.sort(population, new Comparator<Chromosome>() {

			@Override
			public int compare(Chromosome ch1, Chromosome ch2) {
				if (ch1.inversFitness > ch2.inversFitness)
					return 1;
				else if (ch1.inversFitness < ch2.inversFitness)
					return -1;
				else
					return 0;
			}

		});
		float co = 0;
		for (Chromosome chromosome : population) {
			chromosome.acco = co;
			co += chromosome.inversFitness;
		}
		population.get(population.size() - 1).acco = co;
		for (int i = 0; i < 2; i++) {
			float r = (float) (Math.random() * co);
			for (int j = 0; j < population.size() - 1; j++) {
				if (population.get(j).acco <= r && r < population.get(j + 1).acco) {
					towChrom.add(new Chromosome(population.get(j)));
					if (i == 1)
						break;
				}
			}
		}
		return towChrom;
	}

	private static void fitness(Input input, ArrayList<Chromosome> population) {
		for (Chromosome chromosome : population) {
			chromosome.fitness(input);
		}
	}

	private static ArrayList<Chromosome> generatePop(Input input) {
		ArrayList<Chromosome> population = new ArrayList<>();
		for (int i = 0; i < input.populationSize; i++) {
			Chromosome chrom = new Chromosome();
			ArrayList<Float> genes = new ArrayList<>();
			for (int j = 0; j < input.equationDegree + 1; j++) {
				float coff = (float) (input.min + Math.random() * (input.max - input.min));
				genes.add(coff);
			}
			chrom.genes = genes;
			population.add(chrom);
		}
		return population;
	}
}
