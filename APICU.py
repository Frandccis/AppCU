from flask import Flask, request, jsonify
import mysql.connector
import json

#Configuracion incial para la BBDD
config = {
    'user': 'inicio',
    'password': 'C0mpr0b4r',
    'host': '127.0.0.1',
    'autocommit': True,
    'database': 'CU',

}

cnx = mysql.connector.connect(**config)

#Inicio de la API
app = Flask(__name__)

#Con esta direccion podremos tanto iniciar sesion como registrarnos en al app
@app.route('/InicioSesion',methods = ['POST'])
def InicioRegistro():
    try:
        if request.method == 'POST':

            #Recibimos los datos
            data = request.get_json(force=True)
            tipo = data['tipo']                          #Si es un inicio o un registro
            nombre = data['usuario']
            contrasenia = data['contrasenia']
            print("------------------------------------------Inicio Sesion")
            print (tipo)
            print (nombre)
            print (contrasenia)

            #En caso de que sea un inicio de sesion, comprobaremos que el nombre y la contrasenia son correctos y coinciden
            if tipo == 'Ini':
                cur = cnx.cursor()
                cur.execute("Select NombreUsuario from usuario where NombreUsuario = '"+ nombre +"' and Contrasenia = '"+ contrasenia +"'")
                data = cur.fetchall()
                print(data)
                print(len(data))
                print("------------------------------------------Fin Inicio Sesion")
                if len(data) == 1:
                    return 'True'
                cur.close()
                return 'False'

            #Por otra parte si se trata de un registro lo que haremos sera insertar el nombre y la contrasenia en la BBDD
            elif tipo == 'Reg':
                cur = cnx.cursor()
                cur.execute("insert into usuario values ('"+nombre+"', '"+contrasenia+"')")
                cur.execute("Select NombreUsuario from usuario where NombreUsuario = '"+ nombre +"' and Contrasenia = '"+ contrasenia +"'")
                data = cur.fetchall()
                print(data)
                print(len(data))
                print("------------------------------------------Fin Inicio Sesion")
                if len(data) == 1:
                    return 'True'
                cur.close()
                return 'False'

#raise Exception('X') por si es necesario por problemas de la BBDD
    except Exception as e:
        return format(str(e))

#Con esta direccion tan solo comprobamos en la BBDD que el usuario no existe, es un complemento para registrar a un usuario desde la app
@app.route('/ComprobarUsuario',methods = ['POST'])
def ComprobarUsuario():
    try:
        if request.method == 'POST':
            #Recogemos los datos
            data = request.get_json(force=True)
            nombre = data['nombre']
            print("------------------------------------------Comprobar usuario")
            print (nombre)
            
            #Procedemos a comprobar que el nombre de usuario se encuentra o no en la BBDD
            cur = cnx.cursor()
            cur.execute("Select NombreUsuario from usuario where NombreUsuario = '"+ nombre +"'")
            data = cur.fetchall()
            print(data)
            print(len(data))
            print("------------------------------------------Fin Comprobar usuario")
            
            if len(data) == 1:
                return 'False'
            cur.close()
            return 'True'


    except Exception as e:
        return format(str(e))


if __name__ == '__main__':
    #app.run(ssl_context='adhoc') esto para https
    app.run()