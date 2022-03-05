package siscaixaadmin.FileConfig;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author Thiago Henrique de Andrade
 */
public class FileAssistent {
    
    //choose file
    public File chooseOpenFile(String Address,String StrTitle){
        
        String filename = File.separator+"tmp";
        JFileChooser fc = new JFileChooser(new File(filename));
        Component frame = null;
        if(Address != null)fc.setCurrentDirectory(new File(Address));
        if(StrTitle != null)fc.setDialogTitle(StrTitle);
    
        // Mostra a dialog de save file
        fc.showOpenDialog(frame);
        File selFile = fc.getSelectedFile();
        
        return selFile;
    
    }
        
    // Return directory tha current
    
    public static String currentDirProject(){
        return System.getProperty("user.dir");
    }
    
    // cria diretorio

    public static boolean creatDir(String caminho){
        boolean success = (new File(caminho)).mkdirs();
        return success;
    }
    
    // ler tamanho diretorio
    
    public static long tamanhoDiretorio(File dir) {
        long ret = 0;
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                ret += tamanhoDiretorio (f);
            } else {
                ret += f.length();
            }
        }
        return ret;
    }

    public static long sizeDir(String endDir){

        File fileDir = new File(endDir);                
        return tamanhoDiretorio(fileDir);

    }
    
    // remove diretorio
    
    public static boolean deleteDiretorio(File dir) {
        
        if (dir.isDirectory()) {
        String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDiretorio(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    
    public static boolean deleteDir(String endDir){
        return deleteDiretorio(new File(endDir));
    }
    
    // copiar diretorio
    
    public static void copy(File origem, File destino, boolean overwrite) throws IOException {
        if (destino.exists() && !overwrite) {
            return;
        }
        FileInputStream source = new FileInputStream(origem);
        FileOutputStream destination = new FileOutputStream(destino);
        FileChannel sourceFileChannel = source.getChannel();
        FileChannel destinationFileChannel = destination.getChannel();
        long size = sourceFileChannel.size();
        sourceFileChannel.transferTo(0, size, destinationFileChannel);
        destinationFileChannel.close();
    }

    public static boolean copyAll(File origem, File destino, boolean overwrite)  {
        try{
            if (!destino.exists()) {
                destino.mkdir();
            }
            if (!origem.isDirectory()) {
                throw new UnsupportedOperationException("Origem deve ser um diretório");
            }
            if (!destino.isDirectory()) {
                throw new UnsupportedOperationException("Destino deve ser um diretório");
            }
            File[] files = origem.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    copyAll(files[i], new File(destino + "\\" + files[i].getName()), overwrite);
                } else {
                    System.out.println("Copiando arquivo: " + files[i].getName());
                    copy(files[i], new File(destino + "\\" + files[i].getName()), overwrite);
                }
            }
            
            return true;
            
        } catch(Exception e){
            return false;
        }
    }

    public static boolean copyDir(String origem, String destino, boolean subscrever){

        boolean condicao = false;
        
        File fileOrigem  = new File(origem);
        File fileDestino = new File(destino);
        
        if(fileOrigem.exists() && fileDestino.exists()){
            if(!new File(fileDestino+"\\"+fileOrigem.getName()).exists()){
                
                boolean sucesso = creatDir(fileDestino+"\\"+fileOrigem.getName());

                if(sucesso){
                    condicao = copyAll(fileOrigem,new File(fileDestino+"\\"+fileOrigem.getName()),subscrever);
                }
                
            } else {
                condicao = copyAll(fileOrigem,new File(fileDestino+"\\"+fileOrigem.getName()),subscrever);
            }
            
        } else {
            System.out.println("Not Exist!");
            condicao = false;
        }
        return condicao;
    }

    // recortar ditetorio
    
    public static boolean cutDir(String origem, String destino, boolean subscrever){
        
        boolean condicao1, condicao2 = false;
        
        condicao1 = copyDir(origem, destino, subscrever);
        
        while(!condicao2){
            if(condicao1){
                condicao2 = deleteDir(origem);
            }
        }

        return (condicao1 && condicao2);
    }
    
    // renomear diretorio
    
    public static boolean renameDir(String nomeAntigo, String novoNome){
 
        File file = new File(nomeAntigo); 
        File file2 = new File(novoNome);

        boolean success = file.renameTo(file2);
        
        return success;
    }

    /* Retorna um ArrayList com a lista de arquivos contidos em uma pasta
    
     * Retorna na seguinte ordem [nome / tamanho / se é diretorio / ultima data de modificação]
     * alem disso filtra se é diretorio "dir" se é arquivo "file" ou pela extensao ".extencao"
     * para ativar o filtro é necessario colocar um valor no segundo paramentro
     * como por exemplo "dir" para diretorio, "file" para arquivos ou ".extensao" para a extensao expecifica
    */
        
    public static ArrayList listFilesDir(String origem,String extensao){
 
        File origem1 = new File(origem);
        File[] files = origem1.listFiles();
        
        ArrayList nameFiles  = new ArrayList();

        for (int i = 0; i < files.length; ++i) {
            if(extensao != null){
                if(extensao.equals("file")){
                    if(files[i].isFile()){
                        nameFiles.add(files[i].getName());
                        nameFiles.add(files[i].length());
                        nameFiles.add(files[i].isDirectory());
                        nameFiles.add(files[i].lastModified());
                    }
                }
                if(extensao.equals("dir")){
                    if(files[i].isDirectory()){
                        nameFiles.add(files[i].getName());
                        nameFiles.add(sizeDir(files[i].getPath()));
                        nameFiles.add(files[i].isDirectory());
                        nameFiles.add(files[i].lastModified());
                    }
                }
                if(!(extensao.equals("dir")) && !(extensao.equals("file"))){
                    if(files[i].getName().endsWith(extensao)){
                        nameFiles.add(files[i].getName());
                        nameFiles.add(files[i].length());
                        nameFiles.add(files[i].isDirectory());
                        nameFiles.add(files[i].lastModified());
                    }
                }
                
            } else {
                    
                nameFiles.add(files[i].getName());
                if(files[i].isFile()){
                    nameFiles.add(files[i].length());
                } else {
                    nameFiles.add(sizeDir(files[i].getPath()));
                }
                nameFiles.add(files[i].isDirectory());
                nameFiles.add(files[i].lastModified());
            }
        }

        return nameFiles;
    }
    
    // cria um arquivo
    
    public static boolean creatFile(String endereco){
        
        try{
            FileWriter arquivo = new FileWriter(new File(endereco));
            arquivo.close();
            return true;
        } catch (Exception e){
            System.out.println(e);
            return false;
        }
    }
    
    // retorna tamanho de um arquivo
    
    public static long sizeFile(String dir) {
        return new File(dir).length();
    } 
    
    // deleta um arquivo
    
    public static boolean deleteFile(String end){
        
        return new File(end).delete();
        
    }
    
    // copia um arquivo
    
    public static boolean copyFile(String origem, String destino, boolean overwrite){
    
        File origem1  = new File(origem); 
        File destino1 = new File(destino);
        
        try{
            copy(origem1,destino1,overwrite);
            return true;
        } catch (Exception e){
            System.out.println(e);
            return false;
        }
    }
    
    // recorta um arquivo
    
    public static boolean cutFile(String origem, String destino, boolean overwrite){
        
        try {

            boolean condicao1, condicao2 = false;
        
            condicao1 = copyFile(origem,destino,overwrite);

            while(new File(origem).exists()){
                if(condicao1){
                    condicao2 = deleteFile(origem);
                }
            }

            return (condicao1 && condicao2);
            
        } catch (Exception e) {

            System.out.println(e);
            return false;

        }
    }

    // renomear um arquivo
    
    public static boolean renameFile(String nomeAntigo, String novoNome){
        return renameDir(nomeAntigo, novoNome);
    }
    
    // executa arquivo
    
    public static boolean execFile(String file){
        try{
            Process processo = Runtime.getRuntime().exec(file);
            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }
    
    // informacao de um arquivo unico
    
    public static ArrayList infoFile(String origem){
 
        File origem1 = new File(origem);

        ArrayList nameFiles  = new ArrayList();

        if(origem1.isFile()){
            nameFiles.add(origem1.getName());
            nameFiles.add(origem1.length());
            nameFiles.add(origem1.isDirectory());
            nameFiles.add(origem1.lastModified());
        }
        return nameFiles;
    }
    
    // pesquisar arquivo unico em pasta e subpasta
    
    public static ArrayList execPesq(File dir,String Nome) {
        long ret = 0;
        ArrayList nomes = new ArrayList();
        ArrayList l = new ArrayList();
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                l = execPesq(f,Nome);
                for(int i = 0;i<l.size();i++){
                    nomes.add(l.get(i));
                }
            } else {
                 if (Nome.equals(f.getName())) {
                    nomes.add(f.getAbsolutePath());
                 }
                
            }
        }
        return nomes;
    }
    
    public static ArrayList seachFile(String local,String arq){
 
        ArrayList endFiles  = new ArrayList();
        endFiles = execPesq(new File(local),arq);

        return endFiles;
    }

    public static void main(String[] args) {
        System.out.println(deleteDir("C:\\Users\\Thiago\\AppData"));
    }
}