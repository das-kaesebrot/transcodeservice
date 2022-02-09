from flask import Blueprint, jsonify

# REST API routes
rest_api = Blueprint('rest_api', __name__)

@rest_api.route('/')
def index():
    return jsonify({
        "response": "Hello world"
    })
