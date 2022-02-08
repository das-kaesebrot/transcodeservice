#!/usr/bin/env python3
import logging
import handle_requests

def main():
    handle_requests.flask_run()

if __name__ == "__main__":
    logging.basicConfig(format='[%(asctime)s] [%(levelname)s] %(message)s', level=logging.INFO)
    try:
        main()
    except Exception as e:
        logging.error("a")