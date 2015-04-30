package teste;

import org.junit.Test;

public class Teste {

	int tam = 10;
	int total = 1000000;
	int count = 0;
	
	int peso =1;
	int pesoCount = 0;
	
	int asa = 0;

	
	int[] a = new int[tam];
	
	@Test
	public void oi(){
		int i = 0;
		
		for (i=0;i<total;i++){
			
			
			pesoCount++;
			count++;
			if(count%tam==0)
				peso++;
			
			if(pesoCount%peso==0){
				a[(count%tam)] = i;
				pesoCount = 0;
				System.out.println(asa++);
			}
			
			
			
		}
		for (i=0;i<tam;i++){
			System.out.print(" "+a[i] ); 
		}	
		
		
	
	}
	
	
}
