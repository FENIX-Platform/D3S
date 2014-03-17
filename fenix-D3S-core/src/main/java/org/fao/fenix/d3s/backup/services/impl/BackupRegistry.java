package org.fao.fenix.d3s.backup.services.impl;

import org.fao.fenix.d3s.backup.dto.BackupMeta;
import org.fao.fenix.commons.utils.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

@Component
public class BackupRegistry {

    public final static String BACKUP_EXTENSION = ".bak";
    public final static String DDL_EXTENSION = ".ddl";
    private static final Map<String,Map<Integer,File>> index = new HashMap<String, Map<Integer, File>>();
    private static Map<String,Integer> revisionCounters = new HashMap<String, Integer>();
    private static File folder;
    private static File structureFolder;
    private static File tmpFolder;
    private static File dbFolder;

    public static void init(Properties initProperties) {
        folder = new File(initProperties.getProperty("backup.folder"));
        structureFolder = new File(initProperties.getProperty("backup.folder.structure"));
        tmpFolder = new File(initProperties.getProperty("backup.folder.tmp"));
        dbFolder = new File(initProperties.getProperty("databases.folder"));

        if (!folder.exists())
            folder.mkdirs();
        if (!structureFolder.exists())
            structureFolder.mkdirs();
        if (!tmpFolder.exists())
            tmpFolder.mkdirs();

        for (File database : folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && !file.equals(structureFolder) && !file.equals(tmpFolder);
            }
        })) {
            Map<Integer,File> databaseBackups = new HashMap<Integer, File>();
            index.put(database.getName(), databaseBackups);
            int revisionCounter = -1;

            for (File backup : database.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getName().endsWith(BACKUP_EXTENSION);
                }
            })) {
                String fileName = backup.getName();
                Integer revision = new Integer(fileName.substring(0,fileName.length()-BACKUP_EXTENSION.length()));
                if (revision>revisionCounter)
                    revisionCounter = revision;
                databaseBackups.put(revision,backup);
            }
            revisionCounters.put(database.getName(),revisionCounter);
        }
    }

    public Collection<Integer> getRevisions (String database) {
        return index.get(database).keySet();
    }
    public File getFile (String database, Integer revision) {
        return index.containsKey(database) ? index.get(database).get(revision) : null;
    }
    public boolean cointains(String database, Integer revision) {
        return index.containsKey(database) && index.get(database).containsKey(revision);
    }
    public int currentRevision (String database) {
        return revisionCounters.containsKey(database) ? revisionCounters.get(database) : -1;
    }
    public void removeObsoleteRevisions(String database, Integer currentRevision) {
        Integer lastRevision = revisionCounters.get(database);
        if (lastRevision!=null && lastRevision>currentRevision) {
            Map<Integer,File> revisionFiles = index.get(database);
            for (Iterator<Map.Entry<Integer,File>> revisionFileEntries = revisionFiles.entrySet().iterator(); revisionFileEntries.hasNext(); ) {
                Map.Entry<Integer,File> revisionFileEntry = revisionFileEntries.next();
                if (revisionFileEntry.getKey()>currentRevision) {
                    revisionFileEntry.getValue().delete();
                    revisionFileEntries.remove();
                }
            }
        }
    }
    public synchronized boolean addBackup (BackupMeta backup) {
        String database = backup.getDatabase();
        Integer revision = backup.getRevision();
        Map<Integer,File> databaseIndex = index.get(database);
        if (databaseIndex==null) {
            File databaseFolder = new File(folder, database);
            databaseFolder.mkdir();
            index.put(database, databaseIndex = new HashMap<Integer, File>());
        }
        if (!databaseIndex.containsKey(revision)) {
            databaseIndex.put(revision,new File(folder,database+'/'+revision+BACKUP_EXTENSION));
            if (revision>revisionCounters.get(database))
                revisionCounters.put(database,revision);
            return true;
        } else
            return false;
    }


    //Utils
    public File getFile(BackupMeta meta) throws IOException {
        File file = getFile(meta.getDatabase(),meta.getRevision());
        if (file==null) {
            File databaseFolder = new File(folder,meta.getDatabase());
            if (!databaseFolder.exists() || !databaseFolder.isDirectory())
                databaseFolder.mkdirs();
            file = new File(databaseFolder,meta.getRevision()+BACKUP_EXTENSION);
            if (!file.exists())
                file.createNewFile();
        }
        return file;
    }

    public String getDDL (String databaseName) throws IOException {
        final String structureFileName = databaseName+DDL_EXTENSION;

        File[] ddlFiles = structureFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().equalsIgnoreCase(structureFileName) && file.isFile();
            }
        });

        return ddlFiles!=null && ddlFiles.length==1 ? new FileUtils().readTextFile(ddlFiles[0]) : null;
    }

    public File getTmpFolder() {
        return tmpFolder;
    }

    public File getPhysicalDatabsesFolder() {
        return dbFolder;
    }

}
