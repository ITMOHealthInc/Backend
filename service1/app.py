from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/endpoint', methods=['GET'])
def endpoint():
    user_id = request.headers.get('X-User-ID')
    return jsonify({'user_id': user_id})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
