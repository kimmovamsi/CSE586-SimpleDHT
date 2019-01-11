package edu.buffalo.cse.cse486586.simpledht;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;



public class SimpleDhtProvider extends ContentProvider {


    static final String all_avds[] = {"5554", "5556", "5558", "5560", "5562"};
    //static final String remote_port[] = {"11108", "11112", "11116", "11120", "11124"};
    ArrayList<String> remort_port = new ArrayList<String>();
    ArrayList<String> hashed_avd_ids = new ArrayList<String>();
    ArrayList<String> sabka_hashed_ids = new ArrayList<String>();
    ArrayList<String> what_have_we_stored_here = new ArrayList<String>();
    ArrayList<majormc> the_real_slim_shady = new ArrayList<majormc>();
    final int SERVER_PORT = 10000;
    private final String KEY_FIELD = "key";
    private final String VALUE_FIELD = "value";
    String myhashedavdid;
    String myavd, myport;
    String suc,pre, parent;

    class majormc{
        String key;
        String value;
        public majormc(String key, String value){
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub

        if(selection.equals("*")){

            String[] path = getContext().getApplicationContext().fileList();

            for (String aPath : path) {
                getContext().deleteFile(aPath);
            }

            String temp = "deleteall" + "~~~~~~" + myavd;
            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, temp);


        }else if (selection.equals("@")){
            String[] path = getContext().getApplicationContext().fileList();

            for (int i = 0; i < path.length; i++){
                getContext().deleteFile(path[i]);
            }
        }else {
            if (suc.equals(myavd) && pre.equals(myavd)){
                getContext().deleteFile(selection);
            } else {

                if (what_have_we_stored_here.contains(selection)){
                    getContext().deleteFile(selection);
                } else {
                    String temp = "deletethis" + "~~~~~~" + selection;

                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, temp);


                }


            }

        }




        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub

        String hashedkey = null, hashedpre = null;
        try{
            hashedkey = genHash(values.getAsString("key"));
            hashedpre = genHash(pre);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("newinsert");


        System.out.println("test1 - " + hashedkey.compareTo(myhashedavdid));
        System.out.println("test2 - " + hashedkey.compareTo(hashedpre));

        if (!suc.equals(myavd) && !pre.equals(myavd)) {
            if (hashedkey.compareTo(myhashedavdid) <= 0 && hashedkey.compareTo(hashedpre) > 0) {
                FileOutputStream fos;

                try {
                    fos = getContext().openFileOutput(values.getAsString("key"), Context.MODE_PRIVATE);
                    fos.write(values.getAsString("value").getBytes());
                    fos.close();
                } catch (Exception e) {
                    Log.e("Error", "File write failed");
                }

                Log.v("insert", values.toString());

                what_have_we_stored_here.add(values.getAsString("key"));
            } else{
                System.out.println("yaha ka nahi hai yeh " + values.getAsString("key") + "~~~~~~" + values.getAsString("value"));
                String temp = "insert" + "~~~~~~" + values.getAsString("key") + "~~~~~~" + values.getAsString("value");
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, temp);
            }
        }else {
            FileOutputStream fos;

            try {
                fos = getContext().openFileOutput(values.getAsString("key"), Context.MODE_PRIVATE);
                fos.write(values.getAsString("value").getBytes());
                fos.close();
            } catch (Exception e) {
                Log.e("Error", "File write failed");
            }

            Log.v("insert", values.toString());
            what_have_we_stored_here.add(values.getAsString("key"));
        }

        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // TODO Auto-generated method stub

        String collected = null;
        FileInputStream fis = null;
        MatrixCursor mc = new MatrixCursor(new String[] {"key", "value"});

        if (selection.equals("*")){

            System.out.println("Hello");

            String[] path = getContext().getApplicationContext().fileList();

            for (int i = 0; i < path.length; i++){
                try {
                    fis = getContext().openFileInput(path[i]);
                    byte[] input = new byte[fis.available()];
                    while (fis.read(input) != -1) {
                        collected = new String(input);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mc.addRow(new Object[] {path[i], collected});

                System.out.println(path[i] + "  " + collected);
            }

            String popeye = "givemeeverthing" + "~~~~~~" + myavd;
            String testing = null;

            try {
                testing = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, popeye).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (testing.equals("done")){
                for(int i = 0; i<the_real_slim_shady.size(); i++){
                    mc.addRow(new Object[] {the_real_slim_shady.get(i).getKey(), the_real_slim_shady.get(i).getValue()});
                }
            }


        }else if(selection.equals("@")){
            System.out.println("Hola");

            String[] path = getContext().getApplicationContext().fileList();

            for (int i = 0; i < path.length; i++){
                try {
                    fis = getContext().openFileInput(path[i]);
                    byte[] input = new byte[fis.available()];
                    while (fis.read(input) != -1) {
                        collected = new String(input);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mc.addRow(new Object[] {path[i], collected});
                System.out.println(path[i] + "  " + collected);
            }


        } else {

            if (suc.equals(myavd) && pre.equals(myavd)){
                try {
                    fis = getContext().openFileInput(selection);
                    byte[] input = new byte[fis.available()];
                    while (fis.read(input) != -1) {
                        collected = new String(input);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mc.addRow(new Object[] {selection, collected});
                System.out.println(selection + "  " + collected);
            } else {


                if (what_have_we_stored_here.contains(selection)){
                    try {
                        fis = getContext().openFileInput(selection);
                        byte[] input = new byte[fis.available()];
                        while (fis.read(input) != -1) {
                            collected = new String(input);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mc.addRow(new Object[]{selection, collected});
                    System.out.println(selection + "  " + collected);
                } else {
                    String temp = "findthis" + "~~~~~~" + selection;
                    String result = null;
                    try {
                        result = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, temp).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    mc.addRow(new Object[]{selection, result});
                    System.out.println("queryresult - " + result);
                }


            }


        }

        return mc;

    }
    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub

        TelephonyManager tel = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        myavd = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myport = String.valueOf((Integer.parseInt(myavd) * 2));

        try{
            myhashedavdid = genHash(myavd);
            hashed_avd_ids.add(myhashedavdid);
            suc = myavd;
            pre = myavd;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 5; i++){
            try {
                sabka_hashed_ids.add(genHash(all_avds[i]));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(SERVER_PORT));
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
            if (!myavd.equals("5554")){
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,"joinreq");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error", "Can't create a ServerSocket");
        }

        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }


    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(final ServerSocket... sockets) {

            final ServerSocket serverSocket = sockets[0];

            Socket socket;
            ObjectInputStream in;
            ObjectOutputStream out;
            String msglere;
            String[] splitmsg;

            System.out.println("Server ban gaya!");

            try {
                while (true) {
                    socket = serverSocket.accept();
                    in = new ObjectInputStream(socket.getInputStream());
                    out = new ObjectOutputStream(socket.getOutputStream());

                    msglere = (String) in.readObject();

                    Log.d("msg kya mila?", msglere);

                    splitmsg = msglere.split("!#%&");

                    if(splitmsg[0].equals("joinreq")){

                        try{
                            String temp = genHash(splitmsg[1]);
                            remort_port.add(splitmsg[2]);

                            hashed_avd_ids.add(temp);

                            Collections.sort(hashed_avd_ids);
                            int index = hashed_avd_ids.indexOf(myhashedavdid);
                            if (index+1 <= hashed_avd_ids.size()-1 && index-1 >= 0){
                                int index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(index+1));
                                suc = all_avds[index1];
                                index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(index-1));
                                pre = all_avds[index1];
                            }else if(index+1 >= hashed_avd_ids.size()-1 && index-1 >= 0){
                                int index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(0));
                                suc = all_avds[index1];
                                index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(index-1));
                                pre = all_avds[index1];
                            }else if(index+1 <= hashed_avd_ids.size()-1 && index-1 <= 0){
                                int index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(index+1));
                                suc = all_avds[index1];
                                index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(hashed_avd_ids.size()-1));
                                pre = all_avds[index1];
                            }

                            String newnodesuc = null, newnodepre = null;

                            int index2 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(0));
                            parent = all_avds[index2];

                            for(int i = 0; i < remort_port.size(); i++){

                              index = hashed_avd_ids.indexOf(genHash(Integer.toString(Integer.parseInt(remort_port.get(i))/2)));

                                if (index+1 <= hashed_avd_ids.size()-1 && index-1 >= 0){
                                    int index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(index+1));
                                    newnodesuc = all_avds[index1];
                                    index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(index-1));
                                    newnodepre = all_avds[index1];
                                }else if(index+1 >= hashed_avd_ids.size()-1 && index-1 >= 0){
                                    int index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(0));
                                    newnodesuc = all_avds[index1];
                                    index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(index-1));
                                    newnodepre = all_avds[index1];
                                }else if(index+1 <= hashed_avd_ids.size()-1 && index-1 <= 0){
                                    int index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(index+1));
                                    newnodesuc = all_avds[index1];
                                    index1 = sabka_hashed_ids.indexOf(hashed_avd_ids.get(hashed_avd_ids.size()-1));
                                    newnodepre = all_avds[index1];
                                }

                                try{
                                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                            Integer.parseInt(remort_port.get(i)));
                                    out = new ObjectOutputStream(socket.getOutputStream());
                                    out.writeObject("newjoin" + "!#%&" + newnodesuc + "!#%&" + newnodepre + "!#%&" + parent);
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }


                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }


                    } else if(splitmsg[0].equals("newjoin")){

                        suc = splitmsg[1];
                        pre = splitmsg[2];
                        parent = splitmsg[3];

                        System.out.println("suc - " + suc);
                        System.out.println("pre - " + pre);


                    } else if (splitmsg[0].equals("notmine")){


                        if (splitmsg[1].equals(myavd) && splitmsg[2].equals(myport)){
                            try{
                                socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                        Integer.parseInt(Integer.toString(Integer.parseInt(parent)*2)));
                                out = new ObjectOutputStream(socket.getOutputStream());
                                out.writeObject("belongstoparent" + "!#%&" + splitmsg[3] + "!#%&" + splitmsg[4]);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {

                            String hashedkey = null, hashedpre = null;
                            try{
                                hashedkey = genHash(splitmsg[3]);
                                hashedpre = genHash(pre);
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                            System.out.println("test1 - " + hashedkey.compareTo(myhashedavdid));
                            System.out.println("test2 - " + hashedkey.compareTo(hashedpre));


                            if (hashedkey.compareTo(myhashedavdid) <= 0 && hashedkey.compareTo(hashedpre) > 0) {
                                FileOutputStream fos;
                                 try {
                                    fos = getContext().openFileOutput(splitmsg[3], Context.MODE_PRIVATE);
                                    fos.write(splitmsg[4].getBytes());
                                    fos.close();
                                } catch (Exception e) {
                                    Log.e("Error", "File write failed");
                                }
                                 Log.v("insert", splitmsg[3] + " and " + splitmsg[4]);
                                what_have_we_stored_here.add(splitmsg[3]);
                            } else {
                                try {
                                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                            Integer.parseInt(Integer.toString(Integer.parseInt(suc) * 2)));
                                    out = new ObjectOutputStream(socket.getOutputStream());
                                    out.writeObject("notmine" + "!#%&" + splitmsg[1] + "!#%&" + splitmsg[2] + "!#%&" + splitmsg[3] + "!#%&" + splitmsg[4]);
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (splitmsg[0].equals("belongstoparent")){
                        FileOutputStream fos;
                        try {
                            fos = getContext().openFileOutput(splitmsg[1], Context.MODE_PRIVATE);
                            fos.write(splitmsg[2].getBytes());
                            fos.close();
                        } catch (Exception e) {
                            Log.e("Error", "File write failed");
                        }
                        Log.v("insert", splitmsg[1] + " and " + splitmsg[2]);
                        what_have_we_stored_here.add(splitmsg[1]);
                    } else if (splitmsg[0].equals("findthis")){

                        String[] path = getContext().getApplicationContext().fileList();
                        System.out.println("kitne store karra hu? - " + path.length);
                        System.out.println("kitne store karra hu list mai? - " + what_have_we_stored_here.size());

                        if (what_have_we_stored_here.contains(splitmsg[1])){
                            String collected = null;
                            FileInputStream fis = null;

                            try {
                                fis = getContext().openFileInput(splitmsg[1]);
                                byte[] input = new byte[fis.available()];
                                while (fis.read(input) != -1) {
                                    collected = new String(input);
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fis.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            out.writeObject(collected);
                            out.flush();

                            System.out.println("milgaya tha bhai return kiya maine");
                        } else {
                            System.out.println("nai mila toh mai agee bheja");
                            String foundit;
                            String tempo = "findthis" + "~~~~~~" + splitmsg[1];
                            foundit = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, tempo).get();

                            out.writeObject(foundit);
                            out.flush();
                        }

                    }else if (splitmsg[0].equals("givemeeverthing")){
                        if (splitmsg[1].equals(suc)){
                            String collected = null;
                            FileInputStream fis = null;

                            String[] path = getContext().getApplicationContext().fileList();

                            for (String aPath : path) {
                                try {
                                    fis = getContext().openFileInput(aPath);
                                    byte[] input = new byte[fis.available()];
                                    while (fis.read(input) != -1) {
                                        collected = new String(input);
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        fis.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                the_real_slim_shady.add(new majormc(aPath, collected));

                            }
                            System.out.println("majormc ka soize - " + the_real_slim_shady.size());

                            out.writeObject(the_real_slim_shady.size());
                            SystemClock.sleep(10);
                            for (int i = 0; i < the_real_slim_shady.size(); i++){
                                out.writeObject(the_real_slim_shady.get(i).getKey() + "!#%&" + the_real_slim_shady.get(i).getValue());
                            }
                            out.flush();

                        }else {

                            String foundit;
                            String tempo = "givemeeverthing" + "~~~~~~" + splitmsg[1];
                            foundit = new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, tempo).get();
                            if (foundit.equals("done")) {

                                String collected = null;
                                FileInputStream fis = null;

                                String[] path = getContext().getApplicationContext().fileList();

                                for (String aPath : path) {
                                    try {
                                        fis = getContext().openFileInput(aPath);
                                        byte[] input = new byte[fis.available()];
                                        while (fis.read(input) != -1) {
                                            collected = new String(input);
                                        }
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            fis.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    the_real_slim_shady.add(new majormc(aPath, collected));

                                }

                                out.writeObject(the_real_slim_shady.size());
                                SystemClock.sleep(10);
                                for (int i = 0; i < the_real_slim_shady.size(); i++) {
                                    out.writeObject(the_real_slim_shady.get(i).getKey() + "!#%&" + the_real_slim_shady.get(i).getValue());
                                }

                                out.writeObject(foundit);
                                out.flush();
                            }

                        }
                    } else if (splitmsg[0].equals("deletethis")){
                        if (what_have_we_stored_here.contains(splitmsg[1])){
                            getContext().deleteFile(splitmsg[1]);
                        } else {
                            String temp = "deletethis" + "~~~~~~" + splitmsg[1];
                            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, temp);

                        }
                    } else if (splitmsg[0].equals("deleteall")){

                        String[] path = getContext().getApplicationContext().fileList();
                        for (String aPath : path) {
                            getContext().deleteFile(aPath);
                        }

                        if (!suc.equals(splitmsg[1])){
                            String temp = "deleteall" + "~~~~~~" + splitmsg[1];
                            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, temp);

                        }

                    }

                }
            } catch (OptionalDataException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onProgressUpdate(String...strings) {

            return;
        }

    }


    private class ClientTask extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... args) {


            Socket socket;
            ObjectOutputStream out;
            ObjectInputStream in;
            String result;
            String[] temp = args[0].split("~~~~~~");

            if (temp[0].equals("insert")) {
                try {
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(Integer.toString(Integer.parseInt(suc) * 2)));
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("notmine" + "!#%&" + myavd + "!#%&" + myport + "!#%&" + temp[1] + "!#%&" + temp[2]);
                    out.flush();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (temp[0].equals("findthis")){
                try {
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(Integer.toString(Integer.parseInt(suc) * 2)));
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("findthis" + "!#%&" +temp[1]);

                    in = new ObjectInputStream(socket.getInputStream());
                    result = (String) in.readObject();
                    out.flush();

                    return result;

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (temp[0].equals("joinreq")){
                try{
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt("11108"));
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("joinreq" + "!#%&" + myavd + "!#%&" + myport);
                    out.flush();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (temp[0].equals("givemeeverthing")){

                int x;
                String gg;
                String[] jj;

                try{
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(Integer.toString(Integer.parseInt(suc) * 2)));
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("givemeeverthing" + "!#%&" + temp[1]);


                    in = new ObjectInputStream(socket.getInputStream());
                    x = (Integer) in.readObject();

                    for (int i = 0; i < x; i++){
                        gg = (String) in.readObject();
                        jj = gg.split("!#%&");
                        the_real_slim_shady.add(new majormc(jj[0], jj[1]));
                    }





                    out.flush();


                    return "done";




                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } else if (temp[0].equals("deletethis")){
                try{
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(Integer.toString(Integer.parseInt(suc) * 2)));
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("deletethis" + "!#%&" + temp[1]);
                    out.flush();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (temp[0].equals("deleteall")){
                try{
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(Integer.toString(Integer.parseInt(suc) * 2)));
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject("deleteall" + "!#%&" + temp[1]);
                    out.flush();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return "kuchnai";
        }
        protected void onPostExecute(String result) {

        }

    }





}
