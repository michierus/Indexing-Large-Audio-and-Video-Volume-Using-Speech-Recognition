#! /bin/bash

sudo mkdir /mestrado
sudo mkdir /mestrado/statistics
sudo mkdir /mestrado/statistics/producer/
sudo mkdir /mestrado/indir/

cd /mestrado/indir/
sudo wget --no-check-certificate -r 'https://docs.google.com/uc?export=download&id=1E5OnsG1ojb8wk-PQr6XXpOhmjoNunHym' -O audio.wav

cd /mestrado
sudo wget --no-check-certificate -r 'https://docs.google.com/uc?export=download&id=1y0ToAO_C5GvFvk9Lq6YFDvAaTt09xDNC' -O produtor-0.0.1.jar