const http = require("http");
const fs = require("fs");
const path = require("path");

const port = process.env.PORT || 3000;
const root = __dirname;

const types = {
  ".html":"text/html; charset=utf-8",
  ".js":"application/javascript; charset=utf-8",
  ".css":"text/css; charset=utf-8",
  ".json":"application/json; charset=utf-8",
  ".png":"image/png",
  ".jpg":"image/jpeg",
  ".jpeg":"image/jpeg",
  ".svg":"image/svg+xml",
  ".wav":"audio/wav",
  ".mp3":"audio/mpeg"
};

http.createServer((req,res) => {
  let pathname = decodeURIComponent((req.url || "/").split("?")[0]);
  if (pathname === "/") pathname = "/index.html";
  const file = path.normalize(path.join(root, pathname));
  if (!file.startsWith(root)) {
    res.writeHead(403); res.end("Forbidden"); return;
  }
  fs.stat(file, (err, stat) => {
    const target = (!err && stat.isDirectory()) ? path.join(file, "index.html") : file;
    fs.readFile(target, (readErr, data) => {
      if (readErr) {
        res.writeHead(404, {"Content-Type":"text/plain; charset=utf-8"});
        res.end("Not found");
        return;
      }
      res.writeHead(200, {
        "Content-Type": types[path.extname(target).toLowerCase()] || "application/octet-stream",
        "Cache-Control":"public, max-age=300",
        "X-Content-Type-Options":"nosniff"
      });
      res.end(data);
    });
  });
}).listen(port, "0.0.0.0", () => console.log("ABM gallery listening on " + port));
