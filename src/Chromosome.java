import java.util.ArrayList;
public class Chromosome {
	public ArrayList<Float>genes;
	public float fitness;
	public float inversFitness;
	public float acco=0;
	public boolean isChanged=false;
	public float f(float x) {
		float y=genes.get(0);
		for (int i = 1; i <genes.size(); i++) {
			y+=genes.get(i)*Math.pow(x, i);
		}
		return y;
	}

	public float fitness(Input input) {
		float fit=0f;
		for (int i = 0; i < input.points.size(); i++) {
			float newY=f(input.points.get(i).x);
			float delta=newY-input.points.get(i).y;
			delta=(float)Math.pow(delta,2);
			fit+=delta;
		}
		fit/=input.points.size();
		this.fitness=fit;
		this.inversFitness=1/fit;
		return fit;
	}

	public Chromosome(){}
	
	public Chromosome(Chromosome other) {
		this.genes = other.genes;
		this.fitness = other.fitness;
		this.inversFitness = other.inversFitness;
		this.acco = other.acco;
		this.isChanged = other.isChanged;
	}
}
