package com.example.inventarioapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etCodigo, etDescripcion, etPrecio;
    private Button btnRegistrar, btnBuscar, btnEditar, btnBorrar, btnVerTodos;

    private RecyclerView rvArticulos;
    private ArticuloAdapter adaptador;
    private List<Articulo> listaArticulos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCodigo = findViewById(R.id.etCodigo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);

        rvArticulos = findViewById(R.id.rvArticulos);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnVerTodos = findViewById(R.id.btnVerTodos);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarArticulo();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarArticulo();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarArticulo();
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarArticulo();
            }
        });

        btnVerTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarListaArticulos();
            }
        });

        rvArticulos.setLayoutManager(new LinearLayoutManager(this));
    }

    private void registrarArticulo() {
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()) {

            AdminSQLiteOpenHelper admin =
                    new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("precio", precio);

            baseDeDatos.insert("articulos", null, registro);
            baseDeDatos.close();

            etCodigo.setText("");
            etDescripcion.setText("");
            etPrecio.setText("");

            Toast.makeText(this, "Artículo registrado exitosamente",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Debes llenar todos los campos",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarArticulo() {
        String codigo = etCodigo.getText().toString();

        if (!codigo.isEmpty()) {
            AdminSQLiteOpenHelper admin =
                    new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getReadableDatabase();

            android.database.Cursor fila =
                    baseDeDatos.rawQuery(
                            "SELECT descripcion, precio FROM articulos WHERE codigo=" + codigo,
                            null);

            if (fila.moveToFirst()) {
                etDescripcion.setText(fila.getString(0));
                etPrecio.setText(fila.getString(1));
                Toast.makeText(this, "Artículo encontrado",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No existe un artículo con ese código",
                        Toast.LENGTH_SHORT).show();
                etDescripcion.setText("");
                etPrecio.setText("");
            }

            fila.close();
            baseDeDatos.close();
        } else {
            Toast.makeText(this, "Ingresa el código a buscar",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void borrarArticulo() {
        String codigo = etCodigo.getText().toString();

        if (!codigo.isEmpty()) {
            AdminSQLiteOpenHelper admin =
                    new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

            int cantidad = baseDeDatos.delete(
                    "articulos", "codigo=" + codigo, null);

            baseDeDatos.close();

            etCodigo.setText("");
            etDescripcion.setText("");
            etPrecio.setText("");

            if (cantidad == 1) {
                Toast.makeText(this, "Artículo eliminado",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El artículo no existe",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingresa el código a eliminar",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void modificarArticulo() {
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()) {
            AdminSQLiteOpenHelper admin =
                    new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("precio", precio);

            int cantidad = baseDeDatos.update(
                    "articulos", registro, "codigo=" + codigo, null);

            baseDeDatos.close();

            etCodigo.setText("");
            etDescripcion.setText("");
            etPrecio.setText("");

            if (cantidad == 1) {
                Toast.makeText(this, "Artículo actualizado",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se encontró artículo para actualizar",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debes llenar todos los campos",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarListaArticulos() {
        listaArticulos = new ArrayList<>();

        AdminSQLiteOpenHelper admin =
                new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        android.database.Cursor fila =
                bd.rawQuery("SELECT codigo, descripcion, precio FROM articulos", null);

        while (fila.moveToNext()) {
            int codigo = fila.getInt(0);
            String descripcion = fila.getString(1);
            double precio = fila.getDouble(2);

            listaArticulos.add(new Articulo(codigo, descripcion, precio));
        }

        fila.close();
        bd.close();

        adaptador = new ArticuloAdapter(listaArticulos);
        rvArticulos.setAdapter(adaptador);
    }
}
