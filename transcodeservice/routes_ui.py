from transcodeservice import app

# Web ui routes


@app.route('/')
@app.route('/index')
def index():
    return "Hello, World!"
