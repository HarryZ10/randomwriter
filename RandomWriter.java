import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * The program should perform a character based analysis of the provided text(s) to build a language model.
 * The program should then generate output text based on the probability of a character appearing next,
 * given a prefix sequence of k characters.
 * 
 * @author hzhu20@georgefox.edu
 * @since JDK 11
 */

 public class RandomWriter {

    /**
     * Enumerator for error codes
     * 
     * 0 - success
     * 1 - invalid arguments
     * 2 - insufficient text file to satisfy k or n values
     */
    private static enum ExitStatus {
        SUCCESS(0),
        INVALID_ARGUMENTS(1),
        INSUFFICIENT_CHARACTERS(2);
        
        private final int status;
        
        private ExitStatus(int status) {
            this.status = status;
        }
        
        public int getStatus() {
            return status;
        }
    }

    
    /**
     * Your program should support the following command line arguments:
     * 
     * [ prefix length ] [ number of characters to use as prefix, k ]
     * [ output length ] [ number of characters to generate as output, n ]
     * [ input file .. input file 2 .. input file n ]
     * 
     * An example:
     * 
     * java RandomWriter 5 1000 output.txt input1.txt input2.txt input3.txt
     * 
     * @param args command line arguments
     * @throws IOException if an error occurs while reading from the input file
     */
    public static void main(String[] args) throws IOException {

        HashMap<String, ArrayList<Character>> mainModel = new HashMap<String, ArrayList<Character>>();
        ArrayList<String> inputFiles = new ArrayList<>();


        // default values
        int prefixLength = 4;
        int outputLength = 1000;

        // check if arguments are valid
        checkUsage(args);

        try {
            // get prefix length
            prefixLength = Integer.parseInt(args[0]);

            if (prefixLength < 1 || outputLength < 0) {
                System.exit(ExitStatus.INVALID_ARGUMENTS.getStatus());
            }

            // get output length
            outputLength = Integer.parseInt(args[1]);

            // input text file(s) to build language model
            for (int i = 2; i < args.length; i++) {
                inputFiles.add(args[i]);
            }

            // build language model
            mainModel = updateLangModel(inputFiles, mainModel, prefixLength);

            // generate output text
            generateText(mainModel, prefixLength, outputLength);

            System.out.println();
        } catch (NumberFormatException e) {
            System.exit(ExitStatus.INVALID_ARGUMENTS.getStatus());
        } catch (FileNotFoundException e) {
            System.exit(ExitStatus.INVALID_ARGUMENTS.getStatus());
        } catch (IOException e) {
            System.exit(ExitStatus.INVALID_ARGUMENTS.getStatus());
        }

        // exit with success
        System.exit(ExitStatus.SUCCESS.getStatus());
    }

    /**
     * Checks if the command line arguments are valid.
     * 
     * @param args command line arguments
     * @throws IOException if an error occurs while reading from the input file
     */
    private static void checkUsage(String[] args) throws IOException {
        if (args.length < 3) {
            System.exit(ExitStatus.INVALID_ARGUMENTS.getStatus());
        }
    }

    /**
     * Updates the language model with the provided text file.
     * 
     * @param mainModel the language model to update
     * @param prefixLength the length of the prefix to use for the language model
     * @return the updated language model
     * @throws IOException if an error occurs while reading from the input file
     */
    private static HashMap<String, ArrayList<Character>> updateLangModel(ArrayList<String> files,
        HashMap<String, ArrayList<Character>> model,
        int prefixLength) throws IOException {
        
        ArrayList<Character> possibleNextCharacters = new ArrayList<Character>();
        StringBuilder prefix = new StringBuilder();
        FileReader fr;

        // if prefix length is greater than the length of the text file, exit with error
        
        for (String file : files) {

            fr = new FileReader(file);

            // new prefix
            prefix = new StringBuilder();

            for (int i = 0; i < prefixLength; i++) {
                // check if there is enough text to satisfy the prefix length and not a newline character
                if (fr.read() != -1) {
                    prefix.append((char) fr.read());
                } else {
                    System.exit(ExitStatus.INSUFFICIENT_CHARACTERS.getStatus());
                }
            }

            while(fr.ready()) {

                // key is the prefix
                String key = prefix.toString(); 

                // value is the next character
                char nextCharacter = (char) fr.read();

                possibleNextCharacters = model.get(key);

                if (possibleNextCharacters == null) {
                    possibleNextCharacters = new ArrayList<Character>();
                    model.put(key, possibleNextCharacters);
                }

                possibleNextCharacters.add(nextCharacter);

                // update prefix
                prefix = prefix.deleteCharAt(0).append(nextCharacter);
            }

            fr.close();
        }

        return model;                            
    }


    /**
     * Generates output text based on the language model.
     * 
     * @param model the language model to use
     * @param prefixLength the length of the prefix to use for the language model
     * @param outputLength the length of the output text to generate
     * @return the generated output text
     * @throws IOException if an error occurs while writing to the output file
     */
    private static void generateText(HashMap<String, ArrayList<Character>> model,
        int prefixLength, int outputLength) {

        StringBuilder prefix = new StringBuilder();
        ArrayList<Character> possibleNextCharacters = new ArrayList<Character>();

        // choose initial key randomly frm the language model and
        // store it into "prefix"

        Random rand = new Random(); 
        int randomKey = rand.nextInt(model.size());
        prefix.append(model.keySet().toArray()[randomKey]);

        System.out.print(prefix.toString());

        char nextCharacter = ' ';
        int randInteger = 0;
        int counter = prefixLength;

        // while output text is not long enough
        while (counter < outputLength) {

            if (model.containsKey(prefix.toString())) {
                possibleNextCharacters = model.get(prefix.toString());
            } else {
                System.exit(ExitStatus.INSUFFICIENT_CHARACTERS.getStatus());
            }

            // choose a random character from the possible next characters
            randInteger = rand.nextInt(possibleNextCharacters.size());
            nextCharacter = possibleNextCharacters.get(randInteger);

            // add the next character to the output text
            System.out.print(nextCharacter);

            // update prefix
            prefix.deleteCharAt(0);
            
            // append the next character to the prefix
            prefix.append(nextCharacter);

            // increment counter to keep track of output text length
            counter++;
        }
    }

 }
