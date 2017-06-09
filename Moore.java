/************ CLASSE BIANIRE **************/

/**
 * Representation de Moore pour un etat de l'automate
 */
public class Moore{
	
	public Etat etat;
	public int debut;
	public int bilan;
	public int[] transitions;
	
	/**
	 * Construit l'etat de Moore pour un etat
	 * @param etat etat de l'automate
	 * @param taille nb de lettre de l'alphabet de l'automate
	 */
	public Moore(Etat etat, int taille){
		
		this.etat = etat;

		if(etat.isTerm()) this.debut = 2;
		else this.debut = 1;
		
		this.bilan = 0;
		
		this.transitions = new int[taille];
	}
	
}

