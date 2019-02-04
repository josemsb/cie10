package com.cie10.db;

import java.util.ArrayList;
import com.cie10.Datos.Enfermedad;
import com.cie10.Datos.Tabla.CIETable;
import com.cie10.Datos.Tabla.ItemColumns;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class DBAdapter extends Service {

	private final IBinder mBinder = new LocalBinder();
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	ArrayList<Enfermedad> enfermedades = new ArrayList<>();

	public class LocalBinder extends Binder {
		public DBAdapter getService() {			
			return DBAdapter.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {		
		return mBinder;
	}

	@Override
	public void onCreate() {

		dbHelper = new DBHelper(this);		
		db = dbHelper.getDataBase();

	}
	
	@Override
	public void onDestroy() {
		db.close();
		Log.d("DB","onDestroy");
	}


	public static String quitarTildes(String s) {
		//s = Normalizer.normalize(s, Normalizer.Form.NFD);
		//s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s.replace('Á','A').replace('É','E').replace('Í','I').replace('Ó','O').replace('Ú','U');
	}

	public ArrayList<Enfermedad> getEnfermedadesCapitulo(String strCapitulo) {



		String where = " trim(CAPITULO) = '" + strCapitulo + "'";
		ArrayList<Enfermedad> enfermedades = new ArrayList<>();

		Cursor result = db.query(CIETable.TABLE_NAME, CIETable.COLS, where, null, null, null, null);
		if (result.moveToFirst())
			do {

				Enfermedad enfermedad = new Enfermedad();

				enfermedad.setCodigo(result.getString(result.getColumnIndex(ItemColumns.CODIGO)));
				enfermedad.setTitulo(result.getString(result.getColumnIndex(ItemColumns.TITULO)));
				enfermedades.add(enfermedad);

			} while (result.moveToNext());
		result.close();
		return enfermedades;
	}

	public ArrayList<Enfermedad> getEnfermedadesBusqueda(String strBusqueda) {

		strBusqueda = quitarTildes(strBusqueda.toUpperCase());

		String original = strBusqueda.replace(" ","%");
		String[] separated = strBusqueda.split(" ");
		String select = "SELECT CODIGO,TITULO from CIE WHERE ";
		String where;
		String all="";

		String sqlPrimario = select + " replace(replace(replace(replace(replace(titulo, 'Á','A'), 'É','E'),'Í','I'),'Ó','O'),'Ú','U') like '%" + original + "%' or codigo like '%" + original + "%' LIMIT 700 ";
		String sqlSecundario;

		//Log.d("DBHADAPTER",db.toString());

		int cantidad = 0;

		for (int i = 0; i <  separated.length; i++) {
			if(separated[i].trim().length() >0)
			{	cantidad=cantidad+1;
				where = " replace(replace(replace(replace(replace(titulo, 'Á','A'), 'É','E'),'Í','I'),'Ó','O'),'Ú','U') like '%" + separated[i] + "%' or codigo like '%" + separated[i] + "%' ";
				all = all + select + where;
				if (i!=separated.length-1)
				{ all = all + " union "; }
			}
		}

		all = all + " LIMIT 700 ";

		if(cantidad>1)
		{   sqlSecundario = select + " replace(replace(replace(replace(replace(titulo, 'Á','A'), 'É','E'),'Í','I'),'Ó','O'),'Ú','U') like '" + separated[0] + "%' or codigo like '" + separated[0] + "%' LIMIT 700";


			//hice el cambio porque primero va los resultados like= 'campo%'
			Cursor resultPrimario = db.rawQuery(sqlPrimario,null);
			Cursor resultSecundaria = db.rawQuery(sqlSecundario,null);
			Cursor result = db.rawQuery(all,null);

			if (resultPrimario.moveToFirst())
				do {

					Enfermedad enfermedad = new Enfermedad();

					enfermedad.setCodigo(resultPrimario.getString(resultPrimario.getColumnIndex(ItemColumns.CODIGO)));
					enfermedad.setTitulo(resultPrimario.getString(resultPrimario.getColumnIndex(ItemColumns.TITULO)));
					enfermedades.add(enfermedad);

				} while (resultPrimario.moveToNext());

			if (resultSecundaria.moveToFirst())
				do {
					if(buscarEnfermedad(resultSecundaria.getString(resultSecundaria.getColumnIndex(ItemColumns.CODIGO))))
					{
						Enfermedad enfermedad = new Enfermedad();

						enfermedad.setCodigo(resultSecundaria.getString(resultSecundaria.getColumnIndex(ItemColumns.CODIGO)));
						enfermedad.setTitulo(resultSecundaria.getString(resultSecundaria.getColumnIndex(ItemColumns.TITULO)));
						enfermedades.add(enfermedad);

					}

				} while (resultSecundaria.moveToNext());

			if (result.moveToFirst())
				do {
					if(buscarEnfermedad(result.getString(result.getColumnIndex(ItemColumns.CODIGO))))
					{
						Enfermedad enfermedad = new Enfermedad();

						enfermedad.setCodigo(result.getString(result.getColumnIndex(ItemColumns.CODIGO)));
						enfermedad.setTitulo(result.getString(result.getColumnIndex(ItemColumns.TITULO)));
						enfermedades.add(enfermedad);
					}

				} while (result.moveToNext());

			result.close();
			resultSecundaria.close();
			resultPrimario.close();

		}
		else
		{	sqlPrimario= select +  " replace(replace(replace(replace(replace(titulo, 'Á','A'), 'É','E'),'Í','I'),'Ó','O'),'Ú','U') like '" + separated[0] + "%' or codigo like '" + separated[0] + "%'";
			Cursor resultPrimario = db.rawQuery(sqlPrimario,null);
			Cursor result = db.rawQuery(all,null);

			if (resultPrimario.moveToFirst())
				do {
					Enfermedad enfermedad = new Enfermedad();

					enfermedad.setCodigo(resultPrimario.getString(resultPrimario.getColumnIndex(ItemColumns.CODIGO)));
					enfermedad.setTitulo(resultPrimario.getString(resultPrimario.getColumnIndex(ItemColumns.TITULO)));
					enfermedades.add(enfermedad);
				} while (resultPrimario.moveToNext());

			if (result.moveToFirst())
				do {
					if(buscarEnfermedad(result.getString(result.getColumnIndex(ItemColumns.CODIGO))))
					{
						Enfermedad enfermedad = new Enfermedad();

						enfermedad.setCodigo(result.getString(result.getColumnIndex(ItemColumns.CODIGO)));
						enfermedad.setTitulo(result.getString(result.getColumnIndex(ItemColumns.TITULO)));
						enfermedades.add(enfermedad);}

				} while (result.moveToNext());

			result.close();
			resultPrimario.close();

		}
		//Log.d("SQL",sqlPrimario);
		return enfermedades;

	}

	public ArrayList<Enfermedad> getEnfermedadesPreferentes(ArrayList<String> strCampos) {
		//String[] separated = CIETable.CAMPO_BUSQUEDA.split(" ");
		String select = "SELECT CODIGO,TITULO from CIE WHERE trim(CODIGO) IN (";
		String where="";
		String all;

		for (int i = 0; i <  strCampos.size(); i++) {
			where = where + "'" + remove1(strCampos.get(i).trim()) + "'";
			if (i!= strCampos.size()-1)
			{ where = where + ","; }
		}

		all =  select + where + ") ORDER BY CODIGO";

		Cursor result = db.rawQuery(all,null);

		//Cursor result = db.query(CIETable.TABLE_NAME, CIETable.COLS,where, null, null, null, null);

		if (result.moveToFirst())
			do {

				Enfermedad enfermedad = new Enfermedad();

				enfermedad.setCodigo(result.getString(result.getColumnIndex(ItemColumns.CODIGO)));
				enfermedad.setTitulo(result.getString(result.getColumnIndex(ItemColumns.TITULO)));
				enfermedades.add(enfermedad);

			} while (result.moveToNext());

		result.close();
		return enfermedades;
	}

	public static String remove1(String input) {
		String original = "��������������u�������������������";
		String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
		String output = input;
		for (int i=0; i<original.length(); i++) {
			output = output.replace(original.charAt(i), ascii.charAt(i));
		}
		return output;
	}


	public boolean buscarEnfermedad(String codigo) {		
		for (int i=0; i<enfermedades.size() ; i++) {
	       if(enfermedades.get(i).getCodigo().equals(codigo))
	       {
	    	   return false;
	       }
	    }
		
		return true;
	}

}
