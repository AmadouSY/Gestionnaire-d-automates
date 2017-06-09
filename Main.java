import java.io.File;
import java.util.HashSet;
import java.util.Scanner;

/************ CLASSE MAIN **************/

/**
 * Classe principale
 */
public class Main {

	private static Scanner sc;


	public static void main(String[] args){
		sc = new Scanner(System.in);
		start();
	}
	
	/**
	 * Lance le gestionnaire d'automates
	 */
	public static void start(){
		sc = new Scanner(System.in);
		String txt = "\nBienvenu sur le gestionnaire d'automates\n\n";
		txt += "1. Afficher un automate\n";
		txt += "2. Determiniser un automate\n";
		txt += "3. Minimiser un automate\n";
		txt += "4. Comparer deux automates\n";
		txt += "5. Convertir une expression rationnelle en automate\n";
		txt += "6. Generer un automate\n";
		txt += "7. Fermer le programme\n";

		System.out.println(txt);
		System.out.print("Veuillez choisir une action: ");
		int action = sc.nextInt();
		
		//Afficher un automate
		if (action == 1) afficher();
		
		//Determiniser un automate
		else if (action == 2) determinisation();
		
		//Minimiser un automate
		else if (action == 3) minimisation();
		
		//Comparer 2 automates
		else if(action == 4) comparaison();
		
		//Generer un automate
		else if (action == 6) genererAutomate();
		
		//conversion expression vers automate
		else if (action == 5) expressionToAutomate();

		//Quitter le programme
		else if (action == 7) System.out.println("Au revoir\n");
		
		else start();
			
	}
	
	/**
	 * Affiche un automate
	 */
	public static void afficher(){
		sc = new Scanner(System.in);
		System.out.println("\n###### AFFICHAGE D'AUTOMATE ######\n\n");
		System.out.print("Fichier : ");
		String f = sc.nextLine();
		
		if(extraitER(f) == null){
			//Situation ou on a un automate
			Automate A = new Automate(f);
			System.out.println("--- AUTOMATE ---\n"+A);
			sauvegarder(A);
		}else{
			//Situation ou on a une expr
			Arbre Ar1 = Arbre.lirePostfixe(extraitER(f));
			Automate Ar1m = new Automate(Arbre.residuels(Ar1));
			System.out.println("--- AUTOMATE ---\n"+Ar1m);
			sauvegarder(Ar1m);
		}
	}
	
	/**
	 * Compare deux automates
	 */
	public static void comparaison(){
		boolean egaux = false;
		System.out.println("\n##### COMPARAISON DE 2 AUTOMATES ##### \n");
		System.out.print("Fichier 1 : ");
		String f1 = sc.next();
		System.out.print("Fichier 2 : ");
		String f2 = sc.next();
		
		Automate Au1 = null;
		Automate Au2 = null;
		Arbre Ar1 = null;
		Arbre Ar2 = null;
		
		if(extraitER(f1) == null && extraitER(f2) == null){
			// Situation 1: Automate + Automate
			Au1 = new Automate(f1);
			Au2 = new Automate(f2);
			
			Automate Au1m = Au1.minimisation();
			System.out.println("1) Minimalisation par Moore\n\n"+Au1m);
			Automate Au2m = Au2.minimisation();
			System.out.println("2) Minimalisation par Moore\n\n"+Au2m);
			
			if(Au1m != null && Au2m != null) 
				egaux = Au1m.compare(Au2m);

						
		}else if(extraitER(f1) == null && extraitER(f2) != null){
			// Situation 2: Automate + Expr
			Au1 = new Automate(f1);
			Ar2 = Arbre.lirePostfixe(extraitER(f2));
			
			Automate Au1m = Au1.minimisation();
			System.out.println("1) Minimalisation par Moore \n\n"+Au1m);
			Automate Ar2m = new Automate(Arbre.residuels(Ar2));
			System.out.println("2) Minimalisation par Residuels \n\n"+Ar2m);

			if(Au1m != null && Ar2m != null) 
				egaux = Au1m.compare(Ar2m);
			
			
		}else if(extraitER(f1) != null && extraitER(f2) == null){
			// Situation 3: Expr + Automate

			Ar1 = Arbre.lirePostfixe(extraitER(f1));
			Au2 = new Automate(f2);
			
			Automate Ar1m = new Automate(Arbre.residuels(Ar1));
			System.out.println("1) Minimalisation par Residuels \n\n"+Ar1m);
			Automate Au2m = Au2.minimisation();
			System.out.println("2) Minimalisation par Moore \n\n"+Au2m);
			
			if(Ar1m != null && Au2m != null) 
				egaux = Ar1m.compare(Au2m);

			
		}else{
			// Situation 4: Expr + Expr

			Ar1 = Arbre.lirePostfixe(extraitER(f1));
			Ar2 = Arbre.lirePostfixe(extraitER(f2));
			
			Automate Ar1m = new Automate(Arbre.residuels(Ar1));
			System.out.println("1) Minimalisation par Residuels \n\n"+Ar1m);
			Automate Ar2m = new Automate(Arbre.residuels(Ar2));
			System.out.println("2) Minimalisation par Residuels \n\n"+Ar2m);

			if(Ar1m != null && Ar2m != null) 
				egaux = Ar1m.compare(Ar2m);
	
		}
		
		if(egaux){
			System.out.println("Les automates sont egaux\n");
		}else{
			System.out.println("Les automates ne sont pas egaux\n");
		}	
		
		quitter();
	}
	
	/**
	 * Minimise un automate
	 */
	public static void minimisation(){
		System.out.println("#### MINIMISATION D'UN AUTOMATE #### \n");
		System.out.print("Fichier a minimiser : ");
		String f = sc.next();
		
		if(extraitER(f) == null){
			//Situation ou on a un automate
			Automate A = new Automate(f);
			Automate Am = A.minimisation();
			System.out.println("Minimalisation par Moore\n\n"+Am);
			sauvegarder(Am);
		}else{
			//Situation ou on a une expression rationnelle 
			Arbre Ar1 = Arbre.lirePostfixe(extraitER(f));
			Automate Ar1m = new Automate(Arbre.residuels(Ar1));
			System.out.println("Minimalisation par Residuels \n\n"+Ar1m);
			sauvegarder(Ar1m);
		}
	}

	/**
	 * Genere un automate
	 */
	public static void genererAutomate(){
		System.out.println("#### GENERER UN AUTOMATE #### \n");
		System.out.print("Nombre d'etats : ");
		int nbEtat = sc.nextInt();
		System.out.print("Alphabet : ");
		String listeAlphabet = sc.next();
		
		HashSet<Character> alphabet = new HashSet<Character>();
		for(int i = 0; i < listeAlphabet.length(); i++)
			alphabet.add(listeAlphabet.charAt(i));
		
		Automate A = new Automate(nbEtat, alphabet);
		System.out.println("AUTOMATE Aleatoire \n\n"+A);
		
		sauvegarder(A);
	}

	/**
	 * Convertit une expression en automate
	 */
	public static void expressionToAutomate(){
		sc = new Scanner(System.in);
		System.out.println("#### CONVERSION EXPR ----> AUTOMATE #### \n");
		System.out.print("Fichier : ");
		String fichier = sc.next();
		System.out.println("\nQuelle methode souhaitez vous utiliser ?");
		System.out.println("1. Glushkov : ");
		System.out.println("2. Residuels : ");
		System.out.print("Methode : ");
		int methode = sc.nextInt();
		
		//METHODE DE GLUSHKOV
		if(methode == 1){
			System.out.println("\n#### CONVERSION PAR GLUSHKOV #### \n");
			String tmp = extraitER(fichier);
			Arbre arbre = Arbre.lirePostfixe(tmp);

			Automate A = new Automate(arbre);
			System.out.println("Automate selon GLUSHKOV \n\n"+A);
			sc = new Scanner(System.in);

			System.out.println("Voulez vous minimiser l'automate ? (1.Oui/ 2.Non)");
			int min = sc.nextInt();
			if (min == 1){
				Automate Am = A.minimisation();
				System.out.println("Minimalisation par Moore \n\n"+Am);
				sauvegarder(Am);
			}else
				sauvegarder(A);
		}
		
		//METHODE DES RESIDUELS
		if(methode == 2){
			System.out.println("\n#### CONVERSION PAR RESIDUELS #### \n");
			String tmp = extraitER(fichier);
			Arbre arbre = Arbre.lirePostfixe(tmp);

			Automate A = new Automate(Arbre.residuels(arbre));
			System.out.println("Automate selon les residuels \n\n"+A);
			
			sauvegarder(A);
		}
	}
	
	/**
	 * Lit un fichier s'il contient une expression rationnelle
	 * @param fichier le fichier
	 * @return l'expression rationnelle si presente
	 */
	public static String extraitER(String fichier){
		try{
			sc = new Scanner(new File(fichier));
			String ligne = sc.nextLine();
			if(ligne != null && sc.hasNextLine()) return null;
			else return ligne;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Sauvegarde un automate
	 * @param A Automate a sauvegarder
	 */
	public static void sauvegarder(Automate A){
		sc = new Scanner(System.in);
		
		System.out.print("\nSauvegarder l'automate (1.Oui / 2.Non)? ");
		int reponse = sc.nextInt();
		
		if(reponse == 1){
			System.out.print("\nNom du fichier ? ");
			String rep = sc.next();
			System.out.println("L'automate a ete sauvegarder ("+rep+")");
			A.toFile(rep);
			quitter();
		}else{
			quitter();
		}
	}
	
	/**
	 * Demande a l'utilisateur s'il veut quitter le programme
	 */
	public static void quitter(){
		sc = new Scanner(System.in);

		System.out.println("Que voulez vous faire ?\n 1. Retourner a l'acceuil \n 2. Quitter");
		int reponse = sc.nextInt();
		if(reponse == 1){
			start();
		}
		
		if(reponse == 2)
			System.out.println("Au revoir\n");
	}
	
	/**
	 * Determinise un automate
	 */
	public static void determinisation(){
		System.out.println("#### DETERMINISER UN AUTOMATE #### \n");
		System.out.print("Fichier : ");
		String f = sc.next();
		
		if(extraitER(f) == null){
			//Si le fichier contient un automate
			Automate A = new Automate(f);
			Automate Ad = A.determinise();
			System.out.println("Automate deterministe \n\n"+Ad);
			sauvegarder(Ad);
		}else{
			//Situation le fichier contient une expression rationnelle 
			Arbre Ar1 = Arbre.lirePostfixe(extraitER(f));
			Automate Ar1m = new Automate(Arbre.residuels(Ar1));
			Automate Ad = Ar1m.determinise();
			System.out.println("Automate deterministe \n\n"+Ad);
			sauvegarder(Ar1m);
		}
		

	}

}
