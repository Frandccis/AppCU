from flask import Flask, request, jsonify
import mysql.connector
import json

config = {
    'user': 'inicio',
    'password': 'C0mpr0b4r',
    'host': '127.0.0.1',
    'autocommit': True,
    'database': 'CU',

}

cnx = mysql.connector.connect(**config)

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello_World'

@app.route('/test',methods = ['POST', 'GET'])
#curl -X POST -F 'nombre=Gutierrez' http://127.0.0.1:5000/test, esto era para los formularios, ahora obsoleto en este progrma
def test():
    try:
        #raise Exception('X')   por si es necesario por problemas de la BBDD
        if request.method == 'POST':
            #request.content_type("application/json")
            data = request.get_json(force=True)
            nombre = data['nombre']
            print (nombre)
            #-----------------------------------
            
            cur = cnx.cursor()
            cur.execute("INSERT into tabla1 values ('"+ nombre +"')")
            cur.execute("SELECT * from tabla1")
            data = cur.fetchall()
            cur.close()
            return jsonify(data)
        elif request.method == 'GET':
            cur = cnx.cursor()
            cur.execute("SELECT * from tabla1")
            data = cur.fetchall()
            cur.close()
            return jsonify(data)
    except Exception as e:
        return format(str(e))

@app.route('/InicioSesion',methods = ['POST'])
#curl -X POST -F 'nombre=Gutierrez' http://127.0.0.1:5000/test, esto era para los formularios, ahora obsoleto en este progrma
def InicioRegistro():
    try:
        #raise Exception('X')   por si es necesario por problemas de la BBDD
        if request.method == 'POST':
            #request.content_type("application/json")
            data = request.get_json(force=True)
            tipo = data['tipo']                          #Si es un inicio o un registro
            nombre = data['usuario']
            contrasenia = data['contrasenia']
            print (tipo)
            print (nombre)
            print (contrasenia)
            #-----------------------------------

            if tipo == 'Ini':
                cur = cnx.cursor()
                cur.execute("Select NombreUsuario from usuario where NombreUsuario = '"+ nombre +"' and Contrasenia = '"+ contrasenia +"'")
                data = cur.fetchall()
                print(data)
                print(len(data))
                if len(data) == 1:
                    return 'True'
                cur.close()
                return 'False'


            elif tipo == 'Reg':
                cur = cnx.cursor()
                cur.execute("insert into usuario values ('"+nombre+"', '"+contrasenia+"')")
                cur.execute("Select NombreUsuario from usuario where NombreUsuario = '"+ nombre +"' and Contrasenia = '"+ contrasenia +"'")
                data = cur.fetchall()
                print(data)
                print(len(data))
                if len(data) == 1:
                    return 'True'
                cur.close()
                return 'False'

    except Exception as e:
        return format(str(e))

@app.route('/ComprobarUsuario',methods = ['POST'])
def ComprobarUsuario():
    try:
        if request.method == 'POST':
            data = request.get_json(force=True)
            nombre = data['nombre']
            print("------------------------------------------Comprobar usuario")
            print (nombre)
            #------------------------------------
            
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