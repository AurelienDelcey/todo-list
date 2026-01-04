package persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Task;

public class Saver {

    // ----------------------------
    // CHAMPS
    // ----------------------------

    // Mapper Jackson principal (sérialisation / désérialisation)
    private final ObjectMapper mapper;

    // Fichier de sauvegarde (chemin injecté depuis l'extérieur)
    private final File file;

    // Tableau temporaire utilisé lors de la sauvegarde
    private Task[] listToSave;


    // ----------------------------
    // CONSTRUCTEUR
    // ----------------------------

    public Saver(File file) {
        this.mapper = new ObjectMapper();
        this.file = file;
    }


    // ----------------------------
    // GETTERS / SETTERS
    // ----------------------------

    public File getFile() {
        return file;
    }

    public Task[] getListToSave() {
        return listToSave;
    }

    private void setListToSave(Task[] listToSave) {
        this.listToSave = listToSave;
    }


    // ----------------------------
    // SAUVEGARDE
    // ----------------------------

    public void saveAll(Task[] listToSave)
            throws StreamWriteException, DatabindException, IOException {

        // Stockage temporaire (debug / évolution possible)
        setListToSave(listToSave);

        // Création du fichier s'il n'existe pas encore
        if (!file.exists()) {
            file.createNewFile();
        }

        // Sérialisation complète du tableau de tâches en JSON
        mapper.writeValue(file, listToSave);
    }


    // ----------------------------
    // CHARGEMENT (version simple)
    // ----------------------------

    public Task[] load()
            throws StreamReadException, DatabindException, IOException {

        // Désérialisation directe du JSON vers un tableau de Task
        return this.mapper.readValue(file, Task[].class);
    }


    // ----------------------------
    // CHARGEMENT (version streaming - conservée à titre pédagogique)
    // ----------------------------

    /*
    public List<Task> load() throws StreamReadException, DatabindException, IOException {

        List<Task> listLoaded = new ArrayList<Task>();

        System.out.println("create empty list");

        // Création d'un parser JSON bas niveau
        JsonParser parser = this.mapper.createParser(file);

        // MappingIterator permettant de lire les objets un par un
        MappingIterator<Task> it = this.mapper.readValues(parser, Task.class);

        System.out.println("parser and iterator create");

        // Parcours manuel du flux JSON
        while (it.hasNext()) {
            System.out.println("in while, iterator hasnext ok");

            Task task = it.next();
            System.out.println("in while, iterator next ok");

            listLoaded.add(task);
            System.out.println("in while, add to list ok");
        }

        System.out.println("list created" + listLoaded);

        return listLoaded;
    }
    */
}
