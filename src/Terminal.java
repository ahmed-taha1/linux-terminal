import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;


public class Terminal {
    private final Parser parser;
    private final Map<String, Consumer<String[]>> commandActions;
    private final Scanner scanner;
    private final ArrayList<String> commandsHistory;
    private Path currentPath;
    public void echo(String[] args){
        if(args.length == 1){
            System.out.println(args[0]);
        }
        else{
            throw new RuntimeException("ArgumentsError: Echo command accept 1 argument,Received "+args.length);
        }
    }
    public void pwd(String[] args){
        if (args.length != 0) {
            throw new RuntimeException("ArgumentsError: pwd command takes no arguments.");
        }
        System.out.println(currentPath.toAbsolutePath().toString());
    }
    public void cd(String[] args){
        if (args.length > 1) {
            throw new RuntimeException("ArgumentsError: cd command accepts 0 or 1 argument.");
        }
        if (args.length == 0) {
            // Change the current directory to the user's home directory
            currentPath = Paths.get(System.getProperty("user.home"));
        } else if (args[0].equals("..")) {
            Path parentPath = currentPath.getParent();
            if (parentPath != null) {
                currentPath = parentPath;
            } else {
                System.err.println("Already at the root directory, cannot go up.");
            }
        } else {
            // Change the current directory to the specified path
            Path newPath = currentPath.resolve(Paths.get(args[0]));

            // Check if the new path exists and is a directory
            if (Files.exists(newPath) && Files.isDirectory(newPath)) {
                currentPath = newPath;
            } else {
                System.err.println("Directory not found: " + newPath.toString());
            }
        }
    }
    public void ls(String[] args){
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentPath)) {
            for (Path entry : directoryStream) {
                System.out.println(entry.getFileName());
            }
        } catch (IOException e) {
            System.err.println("Error listing contents of the directory: " + e.getMessage());
        }

    }
    public void lsReverse(String []args){
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(currentPath)) {
            ArrayList<Path> entries = new ArrayList<>();
            for (Path entry : directoryStream) {
                entries.add(entry);
            }
            Collections.reverse(entries);
            for (Path entry : entries) {
                System.out.println(entry.getFileName());
            }
        } catch (IOException e) {
            System.err.println("Error listing contents of the directory: " + e.getMessage());
        }
    }
    public void mkdir(String[] args){
        if(args.length < 1){
            throw new RuntimeException("ArgumentsError: mkdir command accept 1 or more arguments ,Received " + args.length);
        }

        for(String arg : args){
            Path path = Paths.get(arg);
            if (!path.isAbsolute()) {
                path = currentPath.resolve(path);
            }

            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                System.err.println("Failed to create the directory: " + e.getMessage());
            }
        }
    }
    public void rmdir(String[] args){
        if(args.length != 1){
            throw new RuntimeException("ArgumentsError: rmdir command accept 1 argument ,Received " + args.length);
        }
        if(args[0].equals("*")){
            File directory = currentPath.toFile();
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        if(file.listFiles().length == 0){
                            try {
                                Files.delete(file.toPath());
                            }
                            catch (IOException e){
                                System.err.println(e.getMessage());
                            }
                        }
                    }
                }
            }
        }

        else {
            Path path = Paths.get(args[0]);
            if (!path.isAbsolute()) {
                path = currentPath.resolve(path);
            }
            File file = path.toFile();
            if(file.isDirectory()){
                if(file.listFiles().length == 0){
                    try {
                        Files.delete(file.toPath());
                    }
                    catch (IOException e){
                        System.err.println(e.getMessage());
                    }
                }
                else {
                    System.err.println("directory is not empty");
                }
            }
            else{
                System.err.println("No Such a directory matches");
            }
        }
    }

    public void rm(String[] args){
        if(args.length != 1){
            throw new RuntimeException("ArgumentsError: rm command accept 1 argument ,Received " + args.length);
        }
        Path path = currentPath.resolve(args[0]);
        try {
            if(Files.exists(path)) {
                Files.delete(path);
            }
            else {
                System.err.println(path.toString() + " NO Such a file!");
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void touch(String[] args){
        if(args.length != 1){
            throw new RuntimeException("ArgumentsError: touch command accept 1 argument(s) ,Received " +args.length);
        }
        File file = new File(args[0]);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
    }
    public void cp(String[] args){
        if(args.length != 2){
            throw new RuntimeException("ArgumentsError: cp command accept 2 argument(s) ,Received " +args.length);
        }
        try {
            File source = new File(args[0]);
            FileWriter destination = new FileWriter(args[1]);
            Scanner fileScanner = new Scanner(source);
            while (fileScanner.hasNext()){
                String line = fileScanner.nextLine();
                destination.write(line);
                destination.write('\n');
            }
            destination.close();
        }catch (IOException exception){
            System.out.println(exception.getMessage());
        }
    }
    public void wc(String[] args){
        if(args.length > 1){
            throw new RuntimeException("ArgumentsError: wc command accept 1 argument(s) ,Received " +args.length);
        }
        try {
            File file = new File(args[0]);
            Scanner fileScanner = new Scanner(file);
            int lines = 0 ;
            int words = 0 ;
            int characters = 0 ;
            while (fileScanner.hasNext()){
                String lineContent = fileScanner.nextLine();
                lines++;
                words += lineContent.split(" ").length;
                characters += lineContent.length();
            }
            System.out.println(lines + " " + words + " " + characters + " " + args[0]);
            fileScanner.close();
        } catch (IOException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }
    public void cat(String[] args){
        if(args.length == 0  || args.length >2){
            throw new RuntimeException("ArgumentsError: cat command Accept 1 or 2 argument(s),Received "+args.length);
        }
        try {
            File file = new File(args[0]);
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNext()){
                System.out.println(fileScanner.nextLine());
            }
            fileScanner.close();
            if(args.length < 2){
                return;
            }
            file = new File(args[1]);
            fileScanner = new Scanner(file);
            while (fileScanner.hasNext()){
                System.out.println(fileScanner.nextLine());
            }
            fileScanner.close();
        }catch (IOException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }
    public void history(String[] args){
        if(args.length > 0 ){
            throw new RuntimeException("ArgumentsError: history command accept 0 argument(s) ,Received " +args.length);
        }
        for(int i =0 ;i<this.commandsHistory.size();i++){
            System.out.println((i+1)+" "+this.commandsHistory.get(i));
        }
    }

    public Terminal(){
        this.parser = new Parser();
        this.commandActions = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.commandsHistory = new ArrayList<>();
        currentPath = Paths.get(System.getProperty("user.dir"));
        createCommandActionsMapping();
    }
    public static void main(String[] args) {
        Terminal terminal = new Terminal();
        terminal.run();
    }
    public void chooseCommandAction(){
        String command = parser.getCommandName();
        String[]args = parser.getArgs();

        // todo remove after testing
        System.out.println("----------Testing--------------");
        System.out.println("command-> "+ command);
        for(int i =0 ;i<args.length;i++){
            System.out.println("args("+(i+1)+")->"+args[i]);
        }
        System.out.println("--------------------------------");
        Consumer<String[]> commandAction = this.commandActions.get(command);
        if(commandAction == null){
            System.out.println("CommandError: command doesn't Exist");
            return;
        }
        commandAction.accept(args);
    }
    public void run(){
        while (true){
            System.out.print(">");
            String input = scanner.nextLine();
            boolean isValidInput = parser.parse(input);
            if(!isValidInput){
                System.out.println("Invalid Command or arguments");
                continue;
            }
            chooseCommandAction();
        }
    }
    private Consumer<String[]> commandWrapper(String commandName,Consumer<String[]> command,ArrayList<String>history){
        return new Consumer<String[]>() {
            @Override
            public void accept(String[] args) {
                try {
                    history.add(0,commandName +" "+ Arrays.toString(args));
                    command.accept(args);
                }catch (RuntimeException error){
                    System.out.println(error.getMessage());
                }
            }
        };
    }
    private void createCommandActionsMapping(){
        commandActions.put("echo", commandWrapper("echo",this::echo,this.commandsHistory));
        commandActions.put("cp", commandWrapper("cp",this::cp,this.commandsHistory));
        commandActions.put("touch", commandWrapper("touch",this::touch,this.commandsHistory));
        commandActions.put("history", commandWrapper("history",this::history,this.commandsHistory));
        commandActions.put("cat", commandWrapper("cat",this::cat,this.commandsHistory));
        commandActions.put("wc", commandWrapper("wc",this::wc,this.commandsHistory));
        commandActions.put("rm", commandWrapper("rm",this::rm,this.commandsHistory));
        commandActions.put("rmdir", commandWrapper("rmdir",this::rmdir,this.commandsHistory));
        commandActions.put("mkdir", commandWrapper("mkdir",this::mkdir,this.commandsHistory));
        commandActions.put("ls", commandWrapper("ls",this::ls,this.commandsHistory));
        commandActions.put("ls -r", commandWrapper("ls -r",this::lsReverse,this.commandsHistory));
        commandActions.put("cd", commandWrapper("cd",this::cd,this.commandsHistory));
        commandActions.put("pwd", commandWrapper("pwd",this::pwd,this.commandsHistory));
    }
}