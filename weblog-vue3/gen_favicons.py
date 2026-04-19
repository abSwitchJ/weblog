import argparse, base64, io, os, sys
from PIL import Image, ImageChops

parser = argparse.ArgumentParser(description='Generate favicon set from a source image.')
parser.add_argument('src', help='source image path (PNG/JPG)')
parser.add_argument('-o', '--out', default=os.path.join(os.path.dirname(os.path.abspath(__file__)), 'public'),
                    help='output directory (default: ./public relative to this script)')
args = parser.parse_args()

if not os.path.isfile(args.src):
    sys.exit(f'source not found: {args.src}')
os.makedirs(args.out, exist_ok=True)

img = Image.open(args.src).convert('RGB')
bg = Image.new('RGB', img.size, (255, 255, 255))
diff = ImageChops.difference(img, bg)
bbox = diff.getbbox()
if bbox:
    img = img.crop(bbox)

w, h = img.size
side = int(max(w, h) * 1.15)
square = Image.new('RGB', (side, side), (255, 255, 255))
square.paste(img, ((side - w) // 2, (side - h) // 2))

for size, name in [(16, 'favicon-16x16.png'),
                   (32, 'favicon-32x32.png'),
                   (180, 'apple-touch-icon.png')]:
    out = square.resize((size, size), Image.LANCZOS)
    out.save(os.path.join(args.out, name), optimize=True)
    print(f'saved {name}  {size}x{size}')

buf = io.BytesIO()
square.save(buf, format='PNG', optimize=True)
b64 = base64.b64encode(buf.getvalue()).decode('ascii')
svg = (f'<svg xmlns="http://www.w3.org/2000/svg" '
       f'viewBox="0 0 {side} {side}">'
       f'<image href="data:image/png;base64,{b64}" '
       f'width="{side}" height="{side}"/></svg>')
with open(os.path.join(args.out, 'favicon.svg'), 'w', encoding='utf-8') as f:
    f.write(svg)
print(f'saved favicon.svg  {side}x{side}')
