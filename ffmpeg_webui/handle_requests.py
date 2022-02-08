from flask import Flask

app = Flask(__name__)

@app.route("/")
def index():
    return "Hello world"

def flask_run():
    app.run(host="0.0.0.0", port=8080, debug=True)
