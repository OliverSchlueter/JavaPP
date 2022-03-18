package de.oliver.javapp.compiler.parser.instructions;

import de.oliver.javapp.compiler.Parser;
import de.oliver.javapp.compiler.parser.Block;
import de.oliver.javapp.compiler.parser.Instruction;
import de.oliver.javapp.compiler.parser.Program;
import de.oliver.javapp.compiler.parser.Variable;
import de.oliver.javapp.exceptions.InvalidTypeException;
import de.oliver.javapp.exceptions.VariableNotFoundException;
import de.oliver.javapp.utils.KeyValue;
import de.oliver.javapp.utils.Node;
import de.oliver.javapp.utils.Token;
import de.oliver.javapp.utils.Word;

public class AssignVariableInstruction extends Instruction {

    private final String variableName;
    private final Node<KeyValue<Word, Token>> ast;
    private final Token valueType;

    public AssignVariableInstruction(Program program, Block block, int line, String variableName, Node<KeyValue<Word, Token>> ast, Token valueType) {
        super(program, block, line);
        this.variableName = variableName;
        this.ast = ast;
        this.valueType = valueType;
    }

    @Override
    public void execute() throws VariableNotFoundException, InvalidTypeException {
        Variable var = block.getVariable(variableName);

        if(var == null){
            throw new VariableNotFoundException(variableName);
        }

        if(var.getType() != valueType && valueType != null){
            throw new InvalidTypeException(var, line, valueType);
        }

        Object value = null;

        switch (var.getType()){
            case TYPE_STRING -> value = Parser.calcStringAst(block, ast);
            case TYPE_FLOAT -> value = (float) Parser.calculateAst(block, ast);
            case TYPE_DOUBLE -> value = Parser.calculateAst(block, ast);
            case TYPE_INTEGER -> value = (int) Parser.calculateAst(block, ast);
            case TYPE_LONG -> value = (long) Parser.calculateAst(block, ast);

            default -> value = Parser.calculateAst(block, ast);
        }

        value = Parser.castToType(value, var.getType());

        var.setValue(value);
    }


    public String getVariableName() {
        return variableName;
    }

    public Node<KeyValue<Word, Token>> getAst() {
        return ast;
    }

    public Token getValueType() {
        return valueType;
    }
}
