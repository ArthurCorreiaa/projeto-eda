/**
 * uma classe genérica de ArrayList criada para criar uma lista de tamanho indeterminado
* e realizar operações simples com ela tais quais, adicionar e remover elementos baseado em indices
* e sem indices
*/
public class ArrayList{
		
	/**
	 * declarando uma lista para numeros inteiros
	 *
	 * @param capacidade serve para determinar o tamanho inicial do array list, também pode ser passado como parâmetro usado no construtor. 
	 * @param last serve para controlarmos o array baseado na última posição da lista
	 **/ 
	private int[] lista;
	private static int capacidade = 20;
	private int last = -1;
	

	/**
	 * construtor onde não passamos o tamanho inicial do Array
	 **/
	public ArrayList(){
		this.lista = new int[this.capacidade];
	}

	/**
	 * construtor onde passamos o tamanho inicial do array
	 **/
	public ArrayList(int capacidade){
		this.capacidade = capacidade;
		this.lista = new int[capacidade];
	}

	/**
	 * 
	 **/
	public boolean isEmpty(){
		return last == -1;
	}
	
	/**
	 *
	 **/
	public boolean isFull(){
		return last == capacidade-1;
	}
	

	/**
	 * adicionando elementos na lista sem uso de indices na última posição da lista
	 *
	 * @param T elemento genérico que será adicionado na lista
	 **/
	public boolean addLast(int elemento){
		if (isFull()){
			resize();
		}
		lista[++last] = elemento;
		return true;
	}

	public boolean addFirst(int elemento){
		shiftDireita(0);
		lista[0] = elemento;
		last++;
		return true;
	}

	public boolean addIndex(int elemento, int index){
		if (index < 0 || index > this.last)
            		throw new IndexOutOfBoundsException();
		shiftDireita(index);
		lista[index] = elemento;
		return true;
	}

	public void shiftDireita(int index){
		if (isFull()){
                        resize();
                }
		for (int i = ++last; i >index; i--){
			lista[i] = lista[i-1];
		}
	}

	public void resize(){
		int[] listaAux = new int[capacidade*=2];
		for (int i = 0; i<lista.length;i++){
			listaAux[i] = lista[i];
		}
		lista = listaAux;
	}

	public boolean contains(int elemento){
		if (isEmpty()){
			return false;
		}
		for(int i = 0; i<=last; i++){
			if(lista[i] == elemento){
				return true;
			}
		}
		return false;
	}
    
  	public int get(int index) {
    		if (index < 0 || index > last) {
        		throw new IndexOutOfBoundsException();
    		}
    		return lista[index];
    	}

  	public int getFirst(){
    		return lista[0];
  	}

  	public int getLast(){
    		return lista[last];
  	}

  	public int size() {
    		return last + 1;
    	}

	public boolean rmvLast(){
		if (isEmpty()){
			throw new NullPointerException("Lista vazia");
		}
		last--;
		return true;
	}

	public boolean rmvFirst(){
		shiftEsquerda(0);
		return true;
	}

	public boolean rmvIndex(int index){
		shiftEsquerda(index);
		return true;
	}

	public void shiftEsquerda(int index){
		if (isEmpty()){
                        throw new NullPointerException("Lista vazia");
                }
		for (int i = index; i<--last; i++){
			lista[i] = lista[i+1];
		}
	}

}	
