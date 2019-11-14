package com.dzenm.helper.file;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.OsHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 文件操作的工具类
 */
public class FileHelper {

    private static final String TAG = FileHelper.class.getSimpleName() + "| ";
    private Context mContext;
    private String mAppFolder;              // app名称目录
    private String mPersonFolder;           // 个人账号文件夹

    private static volatile FileHelper sInstance;

    private FileHelper() {
    }

    public static FileHelper getInstance() {
        if (sInstance == null) synchronized (FileHelper.class) {
            if (sInstance == null) sInstance = new FileHelper();
        }
        return sInstance;
    }

    /**
     * @param context 直接初始化, 将使用App名作为公司名和App文件夹
     * @return this
     */
    public FileHelper init(Context context) {
        init(context, null);                            // 创建以软件名为名称的软件的根目录文件夹
        return this;
    }

    /**
     * @param context 自定义公司名称文件夹
     * @return this
     */
    public FileHelper init(Context context, String companyFolder) {
        init(context, companyFolder, OsHelper.getAppName(context)); // 创建以软件名为名称的软件的根目录文件夹
        return this;
    }

    /**
     * @param appName 自定义公司名称和App文件夹
     * @return this
     */
    public FileHelper init(Context context, String companyFolder, String appName) {
        mContext = context;
        if (TextUtils.isEmpty(companyFolder)) {
            // 创建根目录文件夹(无公司时, 以App名称作为根目录文件夹)
            createAppFolder(appName);
        } else {
            // 创建根目录文件夹(公司+App名称)
            createAppFolder(companyFolder + File.separator + appName);
        }
        return this;
    }

    /**
     * 根据用户不同，创建不同文件夹
     *
     * @param personFolder 个人用户文件夹
     * @return this
     */
    public FileHelper setPersonFolder(String personFolder) {
        mPersonFolder = personFolder;
        return this;
    }

    /**
     * 创建该软件的根目录文件夹
     *
     * @param appPath App名称
     */
    private void createAppFolder(String appPath) {
        if (TextUtils.isEmpty(appPath)) throw new NullPointerException("App 文件夹路径 is null");
        if (!isExternal()) return;

        File file = mkdir(Environment.getExternalStorageDirectory() + File.separator + appPath);

        mAppFolder = file.getAbsolutePath();
    }

    /**
     * 位于/storage/emulated/0/公司名/App文件夹/folders
     *
     * @param folder 创建文件夹的路径, 例: /apk
     * @return this
     */
    public String getPath(String folder) {
        return getFolder(folder).getPath();
    }

    /**
     * 位于/storage/emulated/0/公司名/App文件夹/folders
     *
     * @param folder 创建文件夹的路径, 例: /apk
     * @return this
     */
    public File getFolder(String folder) {
        if (TextUtils.isEmpty(mAppFolder) || TextUtils.isEmpty(folder)) return null;
        if (!folder.startsWith("/")) return null;
        return mkdir(mAppFolder + folder);
    }

    /**
     * 位于/storage/emulated/0/公司名/App文件夹/个人文件夹/folders
     *
     * @param folder 创建文件夹的路径, 例: /image
     * @return this
     */
    public File getPersonFolder(String folder) {
        if (TextUtils.isEmpty(mPersonFolder) || TextUtils.isEmpty(folder)) return null;
        if (!folder.startsWith("/")) return null;
        return mkdir(mAppFolder + File.separator + mPersonFolder + folder);
    }

    /**
     * 获取uri, 适配Android N
     * 必须在Manifest中添加
     * <provider
     * android:name="android.support.v4.content.FileProvider"
     * android:authorities="${applicationId}.provider"
     * android:exported="false"
     * android:grantUriPermissions="true">
     * <meta-data
     * android:name="android.support.FILE_PROVIDER_PATHS"
     * android:resource="@xml/file_provider_path" />
     * </provider>
     *
     * @param file 需要获取uri的文件
     * @return uri
     */
    public Uri getUri(File file) {
        if (OsHelper.isNougat()) {
            // Android 7.0 的文件访问权限问题, FileProvider在xml新建的文件权限表示的含义
            // 例: <external-path name="external" path="." /> 转化后的结果 content://com.dzenm.helper.provider/external/did
            // name 表示名字转化后的根目录路径名称, path表示可以访问的路径, 也就是如下getUriForFile()方法的参数File, 可以访问的路径
            // 其中 . 表示所有文件夹, 也可以具体设置文件夹(必须存在), 访问该文件夹下的文件的路径一定是path下的文件
            return FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * @param uri 当前图片的Uri
     * @return 解析后的Uri对应的String
     */
    public String getRealFilePath(Uri uri) {
        // 1. DocumentProvider
        if (DocumentsContract.isDocumentUri(mContext, uri)) {
            Logger.d(TAG + "file scheme is DocumentProvider");
            // 1.1 ExternalStorageProvider, Whether the Uri authority is ExternalStorageProvider
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // 1.2 DownloadsProvider, Whether the Uri authority is DownloadsProvider
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(mContext, contentUri, null, null);
            }
            // 1.3 MediaProvider, Whether the Uri authority is MediaProvider.
            else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(mContext, contentUri, selection, selectionArgs);
            }
        }
        // 2. MediaStore (and general)
        else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            Logger.d(TAG + "file scheme is MediaStore");
            // 判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.content/...
            if ("com.google.android.apps.photos.content".equals(uri.getAuthority())) {
                return uri.getLastPathSegment();
            }
            // 判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.contentprovider/0/1/mediakey:/local%3A821abd2f-9f8c-4931-bbe9-a975d1f5fabc/ORIGINAL/NONE/1075342619
            else if ("com.google.android.apps.photos.contentprovider".equals(uri.getAuthority())) {
                if (uri.getAuthority() != null) {
                    try (InputStream is = mContext.getContentResolver().openInputStream(uri)) {
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        return writeToTempImageAndGetPathUri(bmp).toString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            } else {                                // 其他类似于media这样的图片，和android4.4以下获取图片path方法类似
                Logger.d(TAG + "file is media");
                return getDataColumn(mContext, uri, null, null);
            }
        }
        // 3. 判断是否是文件形式 File
        else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            Logger.d(TAG + "file scheme is File");
            return uri.getPath();
        }
        return uri.getPath();
    }

    /**
     * 获取图片真实路径, 由于文件uri会存在scheme会content的情况, 但是通过Cursor方法找不到
     * 只能通过Uri的getPath方法获取文件真实路径
     *
     * @param uri           通过Cursor查找文件路径(文件的uri)
     * @param selection     选择哪些列{@link FileProvider}
     * @param selectionArgs 通过mimeType去选择
     * @return 文件uri
     */
    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String filePath = null;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                filePath = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    /**
     * 将图片流读取出来保存到手机本地相册中
     **/
    private Uri writeToTempImageAndGetPathUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                inImage, "title", null);
        return Uri.parse(path);
    }

    /**
     * 获取文件的类型
     *
     * @param filePath 文件路径
     * @return 文件类型
     */
    public String getMimeType(String filePath) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    /**
     * 删除folder文件夹下所有文件(不包括文件夹)
     *
     * @param folder 需要删除文件的File
     * @return 是否删除成功
     */
    public boolean delete(File folder) {
        return delete(folder, "");
    }

    /**
     * 删除path文件夹下所有文件(不包括文件夹)
     *
     * @param path 需要删除文件的File
     * @return 是否删除成功
     */
    public boolean delete(String path) {
        return delete(path, "");
    }

    /**
     * 删除path文件夹下除了filterName文件的所有文件(不包括文件夹)
     *
     * @param path       需要删除文件的路径
     * @param filterName 过滤的文件名称
     * @return 是否删除成功
     */
    public boolean delete(String path, String filterName) {
        return delete(new File(path), filterName);
    }

    /**
     * 删除folder文件夹下除了filterName文件的所有文件(不包括文件夹)
     *
     * @param folder     需要删除文件的File
     * @param filterName 过滤的文件名称
     * @return 是否删除成功
     */
    public boolean delete(File folder, String filterName) {
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) return false;
        Logger.d(TAG + folder.getPath() + "文件夹里共有" + files.length + "个文件");
        boolean isDelete = true;
        for (File file : files) {
            if (file.getName().equals(filterName)) continue;
            if (file.isDirectory()) continue;
            if (file.exists()) isDelete = file.delete();
            if (!isDelete) break;
        }
        Logger.d(TAG + "文件删除" + (isDelete ? "成功" : "失败"));
        return isDelete;
    }

    /**
     * @param file 删除文件夹
     */
    public void deleteFolder(File file) {
        if (!file.exists()) return;
        if (file.isFile()) file.delete();       // 如果是文件直接删除
        if (file.isDirectory()) {               // 如果是目录，递归判断，如果是空目录，直接删除，如果是文件，遍历删除
            File[] children = file.listFiles();
            if (children == null || children.length == 0) {
                file.delete();
                return;
            }
            for (File f : children) {
                delete(f);
            }
            file.delete();
        }
    }

    /**
     * @param bitmap 保存的图片
     * @param path   存储所在的文件夹
     * @return 是否保存成功
     */
    public boolean savePhoto(Bitmap bitmap, String path) {
        return savePhoto(bitmap, new File(path));
    }

    /**
     * @param bitmap    保存的图片
     * @param parent    存储所在的文件夹
     * @param photoName 图片的名称
     * @return 是否保存成功
     */
    public boolean savePhoto(Bitmap bitmap, File parent, String photoName) {
        return savePhoto(bitmap, new File(parent, photoName));
    }

    /**
     * @param bitmap 保存的图片
     * @param file   存储的文件
     * @return 是否保存成功
     */
    public boolean savePhoto(Bitmap bitmap, File file) {
        if (file.exists()) file.delete();
        createNewFile(file);
        Logger.d(TAG + "save the photo's path: " + file.getPath());
        try (FileOutputStream fos = new FileOutputStream(file);) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param parent    文件所在的父级路径
     * @param photoName 图片的名称
     * @return Bitmap 读取到的图片
     */
    public Bitmap getPhoto(File parent, String photoName) {
        return getPhoto(new File(parent, photoName));
    }

    /**
     * @param parent    存储所在的路径
     * @param photoName 图片的名称
     * @return Bitmap 读取到的图片
     */
    public Bitmap getPhoto(String parent, String photoName) {
        return getPhoto(parent + File.separator + photoName);
    }

    /**
     * @param uri 存储所在的路径
     * @return Bitmap 读取到的图片
     */
    public Bitmap getPhoto(Uri uri) {
        return getPhoto(getRealFilePath(uri));
    }

    /**
     * @param path 存储所在的路径
     * @return Bitmap 读取到的图片
     */
    public Bitmap getPhoto(String path) {
        return getPhoto(new File(path));
    }

    /**
     * @param file 文件
     * @return Bitmap 读取到的图片
     */
    public Bitmap getPhoto(File file) {
        if (!isFile(file)) return null;
        Logger.d(TAG + "get the photo's path: " + file.getPath());
        return BitmapFactory.decodeFile(file.getPath());
    }

    /**
     * @return 获取外部存储状态
     */
    public boolean isExternal() {
        //  如果状态不是mounted，无法读写
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 创建新文件
     *
     * @param parent   文件夹路径
     * @param fileName 文件名称
     * @param content  文件内容
     */
    public boolean newFile(String parent, String fileName, String content) {
        return newFile(new File(parent, fileName), content);
    }

    /**
     * 创建新文件
     *
     * @param file    文件
     * @param content 文件内容
     */
    public boolean newFile(File file, String content) {
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osr = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osr)
        ) {
            bw.write(content);
            Logger.d("保存文件成功: " + file.getPath());
            return true;
        } catch (Exception e) {
            Logger.d("保存文件失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建一个List数据的文件
     *
     * @param tArrayList 保存的ArrayList
     * @param filePath   文件路径
     */
    public boolean newFile(ArrayList tArrayList, String filePath) {
        File file = new File(filePath);
        // 新建一个内容为空的文件
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file.toString()))) {
            objectOutputStream.writeObject(tArrayList);
            Logger.d("保存文件成功: " + filePath);
            return true;
        } catch (Exception e) {
            Logger.d("保存文件失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 存储文本到/data/data/包名 文件夹
     *
     * @param fileName 文件名，要在系统内保持唯一
     * @param content  文本内容
     * @return 存储是否成功
     */
    public boolean newFile(String fileName, String content) {
        try (FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
            Logger.d("保存文件成功");
            return true;
        } catch (IOException e) {
            Logger.d("保存文件失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存Serializable对象文件到/data/data/包名 文件夹
     *
     * @param fileName     文件名，要在系统内保持唯一
     * @param serializable serializable对象 (对象必须实现Serializable)
     * @return boolean 存储成功的标志
     */
    public boolean newFile(String fileName, Serializable serializable) {
        try (ObjectOutputStream oos = new ObjectOutputStream(mContext.openFileOutput(fileName, Context.MODE_PRIVATE))) {
            oos.writeObject(serializable);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存Parcelable对象文件到/data/data/包名 文件夹
     *
     * @param fileName   文件名，要在系统内保持唯一
     * @param parcelable parcelable对象 (对象必须实现Parcelable)
     * @return boolean 存储成功的标志
     */
    public boolean newFile(String fileName, Parcelable parcelable) {
        try (FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            @SuppressLint("Recycle") Parcel parcel = Parcel.obtain();
            parcel.writeParcelable(parcelable, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
            byte[] data = parcel.marshall();
            fos.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 保存List对象对象到/data/data/包名 文件夹
     *
     * @param fileName       文件名，要在系统内保持唯一
     * @param parcelableList parcelable列表对象 (对象必须实现Parcelable)
     * @return boolean 存储成功的标志
     */
    public boolean newFile(String fileName, List<Parcelable> parcelableList) {
        try (FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            assert parcelableList != null;
            @SuppressLint("Recycle") Parcel parcel = Parcel.obtain();
            parcel.writeList(parcelableList);
            byte[] data = parcel.marshall();
            fos.write(data);
            return true;
        } catch (IOException e) {
            Logger.d("保存文件失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 重命名文件
     *
     * @param oldFilePath the file which be renamed.
     * @param newFileName target file.
     */
    public boolean rename(String oldFilePath, String newFileName) {
        return rename(new File(oldFilePath), newFileName);
    }

    /**
     * 重命名文件
     *
     * @param oldFile     the file which be renamed.
     * @param newFileName target file.
     */
    public boolean rename(File oldFile, String newFileName) {
        return rename(oldFile, new File(oldFile.getParent(), newFileName));
    }

    /**
     * 重命名文件
     *
     * @param oldFile the file which be renamed.
     * @param newFile target file.
     */
    public boolean rename(File oldFile, File newFile) {
        if (!oldFile.exists()) return false;
        if (newFile.exists()) return false;
        oldFile.renameTo(newFile);
        return true;
    }

    /**
     * 读取文件内容(分行)
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    public String readFile(String filePath) {
        return readFile(new File(filePath));
    }

    /**
     * 读取文件内容(分行)
     *
     * @param file file
     * @return 文件内容
     */
    public String readFile(File file) {
        if (!isFile(file)) return "";
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * @param filePath 文件名称
     * @return 获取本地的ArrayList数据
     */
    public ArrayList readArrayListFile(String filePath) {
        File file = new File(filePath);
        if (!isFile(file)) return null;
        ArrayList savedArrayList = new ArrayList<>();
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            savedArrayList = (ArrayList) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedArrayList;
    }

    /**
     * @param filePath 文件所在路径
     * @return 获取二进制文件流
     */
    public byte[] readBytes(String filePath) {
        File file = new File(filePath);
        if (!isFile(file)) return null;
        byte[] bytes = null;
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            int count;
            byte[] buffer = new byte[4096];
            while ((count = fis.read(buffer)) > 0) {
                bos.write(buffer, 0, count);
            }
            bytes = bos.toByteArray();
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * @param bitmap 图片
     * @return 获取bitmap二进制流
     */
    public byte[] readBytes(Bitmap bitmap) {
        byte[] bytes = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            bytes = out.toByteArray();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 读取Assets文本数据
     *
     * @param fileName 文件名
     * @return String, 读取到的文本内容
     */
    public String readAssets(String fileName) {
        try (InputStream is = mContext.getAssets().open(fileName);
             ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            while (true) {
                int readLength = is.read(buffer);
                if (readLength == -1) break;
                arrayOutputStream.write(buffer, 0, readLength);
            }
            return new String(arrayOutputStream.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储单个数据对象
     *
     * @param fileName 文件名
     * @return Parcelable, 读取到的Parcelable对象，失败返回null
     */
    public Parcelable readParcelable(String fileName, ClassLoader classLoader) {
        try (FileInputStream fis = mContext.openFileInput(fileName);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] b = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            byte[] data = bos.toByteArray();
            @SuppressLint("Recycle") Parcel parcel = Parcel.obtain();
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            return parcel.readParcelable(classLoader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存储数据对象列表
     *
     * @param fileName 文件名
     * @return List, 读取到的对象数组，失败返回null
     */
    public List<Parcelable> readParcelableList(String fileName, ClassLoader classLoader) {
        try (FileInputStream fis = mContext.openFileInput(fileName);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] b = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            byte[] data = bos.toByteArray();
            @SuppressLint("Recycle") Parcel parcel = Parcel.obtain();
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            return parcel.readArrayList(classLoader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取数据对象列表
     *
     * @param fileName 文件名
     * @return Serializable, 读取到的序列化对象
     */
    public Serializable readSerializable(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(mContext.openFileInput(fileName))) {
            return (Serializable) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 复制文件到SD卡
     *
     * @param databaseName 数据库名称
     */
    public boolean copyDBToSDCard(String databaseName) {
        String oldPath = mContext.getDatabasePath(databaseName).getPath();
        File file = getFolder("/Databases");
        if (!file.exists()) file.mkdirs();
        String newPath = file.getPath() + File.separator + databaseName;
        Logger.d(TAG + newPath);
        return copyFile(oldPath, newPath);
    }

    /**
     * 复制文件到DB文件夹
     *
     * @param databaseName 数据库名称
     */
    public boolean copySDCardToDB(String databaseName) {
        File file = getFolder("/Databases");
        if (!file.exists()) file.mkdirs();
        String oldPath = file.getPath() + File.separator + databaseName;
        String newPath = mContext.getDatabasePath(databaseName).getPath();
        return copyFile(oldPath, newPath);
    }

    /**
     * @param oldPath 复制文件的路径
     * @param newPath 新文件路径
     */
    public boolean copyFile(String oldPath, String newPath) {
        return copyFile(new File(oldPath), new File(newPath));
    }

    /**
     * @param oldFile 复制文件的路径
     * @param newPath 新文件路径
     */
    public boolean copyFile(File oldFile, String newPath) {
        return copyFile(oldFile, new File(newPath));
    }

    /**
     * @param oldFile 复制文件的路径
     * @param newFile 新文件路径
     */
    public boolean copyFile(String oldFile, File newFile) {
        return copyFile(new File(oldFile), newFile);
    }

    /**
     * 复制文件
     *
     * @param oldFile 复制文件的路径
     * @param newFile 新文件路径
     */
    public boolean copyFile(File oldFile, File newFile) {
        if (!createNewFile(newFile)) return false;
        if (oldFile.exists()) {         // 文件存在时
            try (InputStream inStream = new FileInputStream(oldFile);
                 FileOutputStream fos = new FileOutputStream(newFile)) {
                byte[] buffer = new byte[1024];
                int len = 0, byteread = 0;
                while ((byteread = inStream.read(buffer)) != -1) {
                    len += byteread; // 字节数 文件大小
                    fos.write(buffer, 0, byteread);
                }
                fos.flush();
                Logger.d(TAG + "复制文件成功: " + newFile.getPath());
                return true;
            } catch (Exception e) {
                Logger.d(TAG + "复制文件出错");
                e.printStackTrace();
            }
        }
        Logger.d(TAG + "复制的源文件不存在");
        return false;
    }

    public void refreshGallery(String filePath) {
        refreshGallery(new File(filePath));
    }

    public void refreshGallery(File file) {
        refreshGallery(Uri.fromFile(file));
    }

    /**
     * 通知广播刷新相册
     *
     * @param uri 刷新的uri
     */
    public void refreshGallery(Uri uri) {
        // 其次把文件插入到系统图库
        File file = new File(getRealFilePath(uri));
        try {
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 以广播方式刷新系统相册，以便能够在相册中找到刚刚所拍摄和裁剪的照片
        if (OsHelper.isKitkat()) {
            MediaScannerConnection.scanFile(mContext, new String[]{file.getAbsolutePath()},
                    new String[]{"image/jpeg", "image/jpg", "image/png"},
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        }
                    });
        } else {
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.fromFile(new File(file.getParent()).getAbsoluteFile())));
        }
    }

    /**
     * @param drawable 需要转化的drawable
     * @return 转化后的bitmap对象
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 计算String的MD5值
     *
     * @param string 需要计算的字符串
     * @return MD5值
     */
    public String md5(String string) {
        if (string.length() < 1) return "";
        try {
            byte[] bytes = MessageDigest.getInstance("MD5Util").digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) temp = "0" + temp;
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 计算文件的MD5值
     *
     * @param file 需要计算的文件
     * @return MD5值
     */
    public String md5(File file) {
        try (FileInputStream ins = new FileInputStream(file)) {
            StringBuilder result = new StringBuilder();
            MappedByteBuffer byteBuffer = ins.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5Util");
            md5.update(byteBuffer);
            byte[] bytes = md5.digest();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) temp = "0" + temp;
                result.append(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean isFile(File file) {
        if (!isExternal()) return false;
        if (!file.exists()) {
            Logger.d(TAG + "file is not exists");
            return false;
        }
        if (file.isDirectory()) {
            Logger.d(TAG + "file is directory");
            return false;
        }
        return true;
    }

    private boolean createNewFile(String filePath) {
        return createNewFile(new File(filePath));
    }

    private boolean createNewFile(File file) {
        if (!isExternal()) return false;
        if (file.exists()) {
            Logger.d(TAG + "file is exists");
            return false;
        } else if (file.isDirectory()) {
            Logger.d(TAG + "file is directory");
            return false;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private File mkdir(String path) {
        File file = new File(path);
        if (file.exists()) return file;
        //  如果该文件夹不存在，则进行创建
        file.mkdirs();
        Logger.d(TAG + "create new folder and the path: " + file.getPath());
        return file;
    }
}