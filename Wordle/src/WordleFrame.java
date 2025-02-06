import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class WordleFrame extends JFrame {
	static int i=0;
    Color rightPositionLetterColor = new Color(0, 255, 0); // Green
    Color wrongPositionLetterColor = new Color(255, 255, 0); // Yellow
    
	public static JButton settingsButton;
	
    public static  int ROWS = 3;
    public static int COLUMNS = 3;
    
    public static ArrayList<ArrayList<JTextField>> Letters = new ArrayList<>();
    public static ArrayList<JTextField> rows;
    public static int turn = 0;

    //Initialize the array with values
    public static char[] Word = new char[COLUMNS]; //{'w', 'a', 'r'};
    public static char[] Guess = new char[COLUMNS] ;
    public static int index=0;
 
    public WordleFrame() {
        // Set up the main frame
        setTitle("Wordle Game");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for title
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("WORDLE", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setPreferredSize(new Dimension(500, 50));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        settingsButton = new JButton("Set");
        settingsButton.setPreferredSize(new Dimension(50, 50));
        topPanel.add(settingsButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center panel for the grid
        JPanel centerPanel = new JPanel(new GridLayout(ROWS, COLUMNS, 10, 10));

        // Create grid of JTextFields
        for (int row = 0; row < ROWS; row++) {
            rows = new ArrayList<>();
            for (int col = 0; col < COLUMNS; col++) {
                JTextField textField = new JTextField(1);
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setFont(new Font("Arial", Font.BOLD, 60));
                textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
                textField.setPreferredSize(new Dimension(70, 70));
                centerPanel.add(textField);
                rows.add(textField);

                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isLetter(c)) {
                            e.consume();  // Ignore non-letter input
                        } else if (textField.getText().length() >= 1) {
                            e.consume();  // Prevent more than 1 letter in the field-and weird bug that last letter doesnt appear
                        } else {
                            // Move focus after the letter is inserted
                            SwingUtilities.invokeLater(() -> moveToNextSquare(textField));
                        }
                    }
                });

                // DocumentListener to capture changes in text field
                /*textField.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                    	if(correctInput(textField.getText())) {
                    		moveToNextSquare(textField);
                    	}
                    	else {//?
                    		System.out.println("not correct input");
                    	}
                        
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                    }

                    //i think we dont need this.
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                    }
                });*/
            }
            Letters.add(rows);
        }

        JPanel paddedPanel = new JPanel(new GridBagLayout());
        paddedPanel.add(centerPanel);
        add(paddedPanel, BorderLayout.CENTER);

        setVisible(true);
        
        //START WORDLE GAME
        playGame();
    }

    //METHODS-FUNCTIONS===================================================================
    public void playGame() {
    	
    	setWord();
    	
        turn = 0;  // Start at the first row
        disableAllSquares();
        enableNextRow();  // Enable the first row
    }
    

    public void disableAllSquares() {
        // Disable all text fields except the first row
        for (int row = 0; row < ROWS; row++) {//row = 1 = first row enabled
            for (int column = 0; column < COLUMNS; column++) {
                Letters.get(row).get(column).setEditable(false);
                Letters.get(row).get(column).setBackground(Color.LIGHT_GRAY);
            }
        }
    }

    
    public void enableNextRow() {
        // Enable text fields in the current row
        for (int column = 0; column < COLUMNS; column++) {
            Letters.get(turn).get(column).setEditable(true);
            Letters.get(turn).get(column).setBackground(Color.white);
        }
        Letters.get(turn).get(0).requestFocus();
    }

    
    public void moveToNextSquare(JTextField currentTextField) {
        System.out.println("word "+Word[index]); 
        Guess[index]=currentTextField.getText().charAt(0);
        System.out.println("Guess "+Guess[index]);
        index++;
        
        
    	// Check if the current text field is filled-typed
        if (!currentTextField.getText().isEmpty()) {
            // Find the index of the current text field
            for (int col = 0; col < COLUMNS; col++) {
                if (Letters.get(turn).get(col) == currentTextField) {
                    // If we're not at the last column, move to the next column
                    if (col < COLUMNS - 1) {
                        Letters.get(turn).get(col + 1).requestFocus();
                    } else if (col == COLUMNS -1) {
                        // If we're at the end of the row, move to the next row
                    	
                        moveToNextRow();
                        
                    }
                    
                    break;
                }
            }
        } 
    }

    
    public void moveToNextRow() {

    	index=0;
    	
    	//CHECK IF WORD IS FOUND
    	//IF GUESS MATCHES THE RANDOMLY SELECTED WORD THEN WIN MESSAGE
    	//ELSE LET THE GAME END LATER IN THIS FUNCTION
    	if(wordGuessed()) {
    		paintLastSquare();//due to bug
    		showCustomDialog(this,true);
    	}
    	else {//WORD NOT FOUND YET
    		// Disable the current row
            for (int column = 0; column < COLUMNS; column++) {
                Letters.get(turn).get(column).setEditable(false);
                Letters.get(turn).get(column).setBackground(Color.LIGHT_GRAY);
            }

            // Move to the next row
            turn++;
            if (turn < ROWS) {
            	paintSquares();
                enableNextRow();  // Enable the next row
            }
            else {//END OF GAME
            	paintLastSquare();//due to bug
            	showCustomDialog(this,false);
            }
    	} 
    	
    	
    }
    
    
    public void paintSquares() {
    	
    	//if(turn==ROWS) turn--;
    	
    	for(int j=0;j<COLUMNS;j++){
    		if(correctPositionLetter(j)) {
        		
        		Letters.get(turn).get(j).setBackground(rightPositionLetterColor);//find rows value to be sent
        	}
        	else if(wrongPositionLetter(j)) {
        		Letters.get(turn).get(j).setBackground(wrongPositionLetterColor);//find rows value to be sent
        	}
    	}
    	
    	
    	
    	/*if (correctPositionLetter()) {
    		Letters.get(0).get(i).setBackground(rightPositionLetterColor);
    		i++;
    	}
    	else if(wrongPositionLetter()){
    			
    	}*/
    }
    
    
    //Created due to bug of not insta painting last square.
    public void paintLastSquare() {
    	if(turn==ROWS) turn--;
    	if(correctPositionLetter(1)) {
    		Letters.get(turn).get(COLUMNS-1).setBackground(rightPositionLetterColor);//find rows value to be sent
    	}
    	else if(wrongPositionLetter(1)) {
    		Letters.get(turn).get(COLUMNS-1).setBackground(wrongPositionLetterColor);//find rows value to be sent
    	}
    	

    }
    
    
    public boolean correctPositionLetter(int index) {
    	if (Word[index]==Guess[index]) return true;
    	return false;
    }
    
    
    public boolean wrongPositionLetter(int index) {//iparxei alla se lathos simeio
    	for(int j=0; j<ROWS; j++) {
    		if(Guess[index]==Word[j]) return true;
    	}
    	return false;
    }
    
    
    public boolean wordGuessed() {
    	if(Arrays.equals(Word, Guess)) return true;
    	return false;
    }
    
    
    public void setWord() {
    	this.Word =getRandomWord();  
    }
    
    
    public char[] getRandomWord() {
    	
    	// Create an ArrayList to hold char arrays
        ArrayList<char[]> charArrayList = new ArrayList<>();

        // Initialize char arrays and add them to the ArrayList
        charArrayList.add(new char[] {'c', 'a', 't'}); // cat
        charArrayList.add(new char[] {'d', 'o', 'g'}); // dog
        charArrayList.add(new char[] {'b', 'a', 't'}); // bat
        charArrayList.add(new char[] {'r', 'a', 't'}); // rat
        charArrayList.add(new char[] {'h', 'a', 't'}); // hat
        charArrayList.add(new char[] {'s', 'u', 'n'}); // sun
        charArrayList.add(new char[] {'m', 'o', 'n'}); // mon
        charArrayList.add(new char[] {'p', 'e', 'n'}); // pen
        charArrayList.add(new char[] {'l', 'e', 't'}); // let

        // Create a Random object
        Random random = new Random();

        // Get a random index within the bounds of the ArrayList
        int randomIndex = random.nextInt(charArrayList.size()); // Generates a random index based on size
        char[] randomWord = charArrayList.get(randomIndex); // Get the random char array

        // Convert char[] to String for printing
        String randomWordString = new String(randomWord);
        System.out.println("Random Word: " + randomWordString);
    	
    	return randomWord;
    }
    
    
    //Method to show the custom game dialog
    public static void showCustomDialog(JFrame parent,boolean found) {
    	
    	// Create a new dialog
        JDialog dialog = new JDialog(parent, "Game Status", true);
        dialog.setLayout(new BorderLayout());

        // Create a label for the message
        JLabel messageLabel;
        if(found) {//Word found
        	messageLabel = new JLabel("<html>Word Found!<br>Continue to next Game?</html>", SwingConstants.CENTER);
        }
        else {
        	messageLabel = new JLabel("<html>You have Lost!<br>Try Again?</html>", SwingConstants.CENTER);
        }
        
        

        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        JButton nextStateButton;
        if(found) {//Word found
        	nextStateButton = new JButton("Next Game");
        }
        else {
        	nextStateButton = new JButton("Try Again");
        }
        JButton backToMainButton = new JButton("Back to Main Menu");
        
        

        // Add buttons to the panel
        buttonPanel.add(backToMainButton);
        buttonPanel.add(nextStateButton);
        

        // Add components to the dialog
        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Handle Next Game button click
        nextStateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(dialog, "Starting the next game...");
                dialog.dispose(); // Close the dialog
            }
        });

        // Handle Back to Main Menu button click
        backToMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(dialog, "Returning to the main menu...");
                dialog.dispose(); // Close the dialog
            }
        });

        // Set dialog properties
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WordleFrame::new);
    }
}
