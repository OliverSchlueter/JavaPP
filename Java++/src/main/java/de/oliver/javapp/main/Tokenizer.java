package de.oliver.javapp.main;

import de.oliver.javapp.exceptions.ForbiddenSymbolException;
import de.oliver.javapp.utils.KeyValue;
import de.oliver.javapp.utils.Token;
import de.oliver.javapp.utils.Word;

import java.util.*;

public class Tokenizer {

    private final Map<Integer, LinkedList<Word>> words;
    private final Map<Integer, LinkedList<KeyValue<Word, Token>>> tokens;

    public Tokenizer(Map<Integer, LinkedList<Word>> words) {
        this.words = words;
        this.tokens = new HashMap<>();
    }

    public Map<Integer, LinkedList<KeyValue<Word, Token>>> tokenize() throws ForbiddenSymbolException {

        for (Map.Entry<Integer, LinkedList<Word>> entry : words.entrySet()) {
            int line = entry.getKey();;
            LinkedList<Word> wordList = entry.getValue();

            wordLoop:
            for (Word word : wordList) {
                for (Token token : Token.withStr()) {
                    if(word.value().equals(token.getStr())){
                        addToken(line, word, token);
                        continue wordLoop;
                    }
                }

                if(isInteger(word.value())){
                    addToken(line, word, Token.INTEGER);
                    continue;
                }

                 // TODO: check if there are forbidden symbols in function name
                if(word.value().contains("(") && word.value().endsWith(")")){
                    addToken(line, word, Token.CALL_FUNCTION);
                    continue;
                }

                if(word.value().startsWith("\"") && word.value().endsWith("\"")){
                    addToken(line, word, Token.STRING);
                    continue;
                }

                if(word.value().startsWith("'") && word.value().endsWith("'")){
                    addToken(line, word, Token.TYPE_CHARACTER);
                    continue;
                }

                List<String> forbiddenCharsInIdentifier = Arrays.asList(
                        "\"", "(", ")", "[", "]", "{", "}", "=", "+", "-", "*", "/"
                );

                for (String s : forbiddenCharsInIdentifier) {
                    if(word.value().contains(s)){
                        throw new ForbiddenSymbolException(word, s);
                    }
                }

                addToken(line, word, Token.IDENTIFIER);
            }
        }

        return tokens;
    }

    private boolean isInteger(String str){
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e){
            return false;
        }

        return true;
    }

    private void addToken(int line, Word word, Token token){

        if(tokens.containsKey(line)){
            LinkedList<KeyValue<Word, Token>> lineWords = this.tokens.get(line);
            lineWords.add(new KeyValue<>(word, token));
        } else {
            LinkedList<KeyValue<Word, Token>> lineWords = new LinkedList<>();
            lineWords.add(new KeyValue<>(word, token));
            tokens.put(line, lineWords);
        }
    }

}
