import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private String commandName;
    private String[] args;
    public boolean parse(String input){
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(input.split(" ")));
        ArrayList<String> argsList = new ArrayList<>();
        if(parts.isEmpty()){
            return false;
        }
        this.commandName = parts.get(0);
        if(parts.size() > 2 && parts.get(1).startsWith("-")){
            this.commandName += " " + parts.get(1);
        } else if (parts.size() > 1){
            argsList.add(parts.get(1));
        }
        for(int i = 2 ;i<parts.size();i++){
            argsList.add(parts.get(i));
        }
        this.args = argsList.toArray(new String[0]);
        return true;
    }
    public String getCommandName(){
        return  this.commandName;
    }
    public String[] getArgs(){
        return this.args;
    }
    //    private final Map<String,int[]> commandsArgumentLengthMapping = new HashMap<>();

//    private void createCommandArgumentsMapping(){
//        this.commandsArgumentLengthMapping.put("echo",new int[]{1});
//        this.commandsArgumentLengthMapping.put("pwd",new int[]{0});
//        this.commandsArgumentLengthMapping.put("cd",new int[]{0,1});
//        this.commandsArgumentLengthMapping.put("ls",new int[]{0});
//        this.commandsArgumentLengthMapping.put("ls -r",new int[]{0});
//        this.commandsArgumentLengthMapping.put("mkdir",new int[]{}); /// infinite / any args
//        this.commandsArgumentLengthMapping.put("rmdir",new int[]{1});
//        this.commandsArgumentLengthMapping.put("touch",new int[]{1});
//        this.commandsArgumentLengthMapping.put("cp",new int[]{2,2});
//        this.commandsArgumentLengthMapping.put("cp -r",new int[]{2,2});
//        this.commandsArgumentLengthMapping.put("rm",new int[]{1});
//        this.commandsArgumentLengthMapping.put("cat",new int[]{2});
//        this.commandsArgumentLengthMapping.put("wc",new int[]{1});
//
//        this.commandsArgumentLengthMapping.put("history",new int[]{0});
//    }
//    private boolean isValidCommand(String commandName,int argsLength){
//        int[] validArgsSizes = this.commandsArgumentLengthMapping.get(commandName);
//        if(validArgsSizes == null){
//            return false;
//        }
//        if(validArgsSizes.length == 0){
//            return true;
//        }
//        for (int validArgsSize : validArgsSizes) {
//            if (argsLength == validArgsSize) {
//                return true;
//            }
//        }
//        return false;
//    }
}
