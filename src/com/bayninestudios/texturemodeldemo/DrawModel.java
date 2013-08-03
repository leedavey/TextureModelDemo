package com.bayninestudios.texturemodeldemo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class DrawModel
{
    private final FloatBuffer mVertexBuffer;
    private final ShortBuffer mIndexBuffer;
    private final FloatBuffer mTexBuffer;

    public DrawModel(Context context, int resId)
    {
        ArrayList<String> vertexes = new ArrayList<String>();
        ArrayList<String> textures = new ArrayList<String>();
        ArrayList<String> faces = new ArrayList<String>();

        float[] vCoords;
        short[] iCoords;
        float[] tCoords;
        int vertexIndex = 0;
        int faceIndex = 0;
        int textureIndex = 0;

        InputStream iStream = context.getResources().openRawResource(resId);
        InputStreamReader isr = new InputStreamReader(iStream);
        BufferedReader bReader = new BufferedReader(isr);
        String line;
        try {
            while (( line = bReader.readLine()) != null) {
                if (line.startsWith("v ")) vertexes.add(line.substring(2));
                if (line.startsWith("vt ")) textures.add(line.substring(3));
                if (line.startsWith("f ")) faces.add(line.substring(2));
            }
        } catch (IOException e) {
        }

        vCoords = new float[faces.size() * 3 * 3];
        tCoords = new float[faces.size() * 3 * 2];
        iCoords = new short[faces.size() * 3];

        // for each face
        for (int i = 0; i < faces.size(); i++) {
            String[] faceSplit = faces.get(i).split(" ");
            // for each face component
            for (int j = 0; j < faceSplit.length; j++) {
                iCoords[faceIndex] = (short)faceIndex;
                faceIndex++;
                String[] faceComponent = faceSplit[j].split("/");

                String vertex = vertexes.get(Integer.parseInt(faceComponent[0])-1);
                String texture = textures.get(Integer.parseInt(faceComponent[1])-1);
                String vertexComp[] = vertex.split(" ");
                String textureComp[] = texture.split(" ");
                for (int v = 0; v < vertexComp.length; v++) {
                    vCoords[vertexIndex] = Float.parseFloat(vertexComp[v]);
                    vertexIndex++;
                }
                for (int t = 0; t < textureComp.length; t++) {
                    tCoords[textureIndex] = Float.parseFloat(textureComp[t]);
                    textureIndex++;
                }
            }
        }

        mVertexBuffer = makeFloatBuffer(vCoords);
        mIndexBuffer = makeShortBuffer(iCoords);
        mTexBuffer = makeFloatBuffer(tCoords);

    }

    private FloatBuffer makeFloatBuffer(float[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    private ShortBuffer makeShortBuffer(short[] arr)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer ib = bb.asShortBuffer();
        ib.put(arr);
        ib.position(0);
        return ib;
    }

    public void draw(GL10 gl)
    {
        gl.glFrontFace(GL10.GL_CCW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, mIndexBuffer.remaining(), GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
    }
}

